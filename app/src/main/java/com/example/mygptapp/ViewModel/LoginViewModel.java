package com.example.mygptapp.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.mygptapp.Database.AppDatabase;
import com.example.mygptapp.Uils.HashUtils;
import com.example.mygptapp.Uils.JsonUtil;
import com.example.mygptapp.pojo.MyResponse;
import com.example.mygptapp.pojo.UserEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<UserEntity> user = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public LoginViewModel() {
        user.setValue(new UserEntity());
    }

    public MutableLiveData<UserEntity> getUser() {
        return user;
    }

    public void setUser(UserEntity userEntity) {
        this.user.setValue(userEntity);
    }


    public void login(String username, String password, Context context) {
        // 向后端发起请求
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        password = HashUtils.MD5Hash(password);
        RequestBody body = RequestBody.create(mediaType, "{\"userName\":\"" + username + "\",\"password\":\"" + password + "\"}");

        Request request = new Request.Builder()
                .url("http://47.99.133.237:8080/user/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // 发送请求
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                error.postValue(e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if(response.isSuccessful()) {
                    String json = response.body().string();
                    MyResponse res = JsonUtil.parseJson(json);
                    String status = res.getStatus();
                    if(status.equals("success")) {
                        JsonElement data = res.getData();
                        UserEntity userEntity1 = new UserEntity();
                        JsonObject jsonObject = data.getAsJsonObject();
                        userEntity1.setUserID(jsonObject.get("userID").getAsInt());
                        userEntity1.setUserName(jsonObject.get("userName").getAsString());
                        userEntity1.setMobilePhone(jsonObject.get("mobilePhone").getAsString());
                        userEntity1.setDefaultModel(jsonObject.get("defaultModel").getAsString());
                        userEntity1.setDefaultPrompt(jsonObject.get("defaultPrompt").getAsString());
                        String token = response.header("set-cookie");
                        token = token.split(";")[0];
                        userEntity1.setToken(token);
                        user.postValue(userEntity1);

                        // 保存token
                        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "user").build();
                        db.userDao().deleteAll();
                        db.userDao().insert(userEntity1);
                        db.close();
                    } else {
                        error.postValue(res.getMessage());
                    }
                } else {
                    error.postValue("登录失败");
                }
            }
        });
    }

    public LiveData<String> getError() {
        return error;
    }


    public void autoLogin(Activity loginActivity) {
        // 获取token
        AppDatabase db = Room.databaseBuilder(loginActivity, AppDatabase.class, "user").build();
        AtomicReference<String> token = new AtomicReference<>("");

        Thread t = new Thread(() -> {
            token.set(db.userDao().getToken());
            if(token.get() == null) {
                return;
            }
            if (!token.get().equals("")) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://47.99.133.237:8080/user/login")
                        .addHeader("Cookie", token.get())
                        .build();
                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                        error.postValue(e.getMessage());
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String json = response.body().string();
                            MyResponse res = JsonUtil.parseJson(json);
                            String status = res.getStatus();
                            if (status.equals("success")) {
                                JsonElement data = res.getData();
                                JsonObject jsonObject = data.getAsJsonObject();
                                user.getValue().setUserID(jsonObject.get("userID").getAsInt());
                                user.getValue().setUserName(jsonObject.get("userName").getAsString());
                                user.getValue().setMobilePhone(jsonObject.get("mobilePhone").getAsString());
                                user.getValue().setDefaultModel(jsonObject.get("defaultModel").getAsString());
                                user.getValue().setDefaultPrompt(jsonObject.get("defaultPrompt").getAsString());
                                user.getValue().setToken(token.get());
                                user.postValue(user.getValue());
                            } else {
                                error.postValue(res.getMessage());
                            }
                        } else {
                            error.postValue("登录失败");
                        }
                    }
                });
            }
            db.close();
        });

        // 等待线程结束
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MyResponse updateUserSettings(String model, String prompt) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"userID\":" + user.getValue().getUserID() + ",\"defaultModel\":\"" + model + "\",\"defaultPrompt\":\"" + prompt + "\"}");

        Request request = new Request.Builder()
                .url("http://47.99.133.237:8080/user/updateUserSettings")
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", user.getValue().getToken())
                .build();

        AtomicReference<MyResponse> retRes = new AtomicReference<>(new MyResponse());

        Thread t = new Thread(() -> {
            try {
                okhttp3.Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    retRes.set(JsonUtil.parseJson(json));
                } else {
                    retRes.set(new MyResponse("error", "更新失败"));
                }
            } catch (IOException e) {
                retRes.set(new MyResponse("error", e.getMessage()));
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return retRes.get();
    }


    public MyResponse register(String username, String password, String mobilePhone) {
        String hashedPassword = HashUtils.MD5Hash(password);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"userName\":\"" + username + "\",\"mobilePhone\":\"" + mobilePhone + "\",\"password\":\"" + hashedPassword + "\"}");
        Request request = new Request.Builder()
                .url("http://47.99.133.237:8080/user/register")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        AtomicReference<MyResponse> res = new AtomicReference<>(new MyResponse());
        Thread t = new Thread(() -> {
            try {
                okhttp3.Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    res.set(JsonUtil.parseJson(json));
                } else {
                    res.set(new MyResponse("error", "注册失败"));
                }
            } catch (IOException e) {
                res.set(new MyResponse("error", e.getMessage()));
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res.get();
    }
}
