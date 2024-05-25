package com.example.mygptapp.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.mygptapp.Database.AppDatabase;
import com.example.mygptapp.Uils.JsonUtil;
import com.example.mygptapp.pojo.ChatInfo;
import com.example.mygptapp.pojo.MyResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<List<ChatInfo>> chatInfoList;

    public ChatViewModel() {
        chatInfoList = new MutableLiveData<>();
        chatInfoList.setValue(new ArrayList<>());
    }

    public MutableLiveData<List<ChatInfo>> getChatInfoList() {
        return chatInfoList;
    }

    public void getAllChatInfo(Context context) throws InterruptedException {
        // 删除原有的chatInfoList
        chatInfoList.setValue(new ArrayList<>());

        // 获得token
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "user").build();
        Thread t = new Thread(() -> {
            String token = db.userDao().getToken();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://47.99.133.237:8080/user/getAllChatList")
                    .get()
                    .addHeader("cookie", token)
                    .build();

            try {
                okhttp3.Response response = client.newCall(request).execute();
                String res = response.body().string();
                MyResponse myResponse = JsonUtil.parseJson(res);
                if(myResponse.getStatus().equals("success")) {
                    JsonElement data = myResponse.getData();
                    JsonArray jsonArray = data.getAsJsonArray();
                    // 从jsonArray中解析出chatInfoList
                    for(JsonElement jsonElement : jsonArray) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setChatID(jsonObject.get("chatID").getAsString());
                        chatInfo.setChatTitle(jsonObject.get("chatTitle").getAsString());
                        chatInfo.setChatModel(jsonObject.get("chatModel").getAsString());
                        chatInfo.setTime(jsonObject.get("time").getAsString());

                        if(chatInfoList.getValue() == null) {
                            chatInfoList.setValue(new ArrayList<>());
                        }
                        chatInfoList.getValue().add(chatInfo);
                        chatInfoList.postValue(chatInfoList.getValue());
                    }
                } else {
                    Log.e("ChatViewModel", myResponse.getMessage());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        t.start();
        t.join();
    }

//    function sendImageQuestion() {
//    if (ifRunning.value) {
//        ElMessage({
//            message: '正在生成，请稍后',
//            type: 'warning'
//        })
//    }
//
//    // 禁止输入框输入
//    ifRunning.value = true
//
//    axios.post("http://47.99.133.237:8080/imageCreation", {
//        prompt: inputText.value,
//        size: "1024*1024",
//        n: 1
//    }, {
//        withCredentials: true,
//        headers: {
//            'Content-Type': 'application/x-www-form-urlencoded'
//        }
//    }).then(res => {
//        if (res.data.status === 'success') {
//            outputImageUrl.value = res.data.data
//            ifRunning.value = false
//        } else {
//            ElMessage({
//                message: res.data.message,
//                type: 'error'
//            })
//            ifRunning.value = false
//        }
//    }).catch(err => {
//        console.log(err)
//    })
//}
    public void sendImageMessage(String prompt, Context context, okhttp3.Callback callback) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "user").build();
        Thread t = new Thread(() -> {
            String token = db.userDao().getToken();
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)  // 设置读取超时时间
                    .writeTimeout(60, TimeUnit.SECONDS) // 设置写入超时时间
                    .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "prompt=" + prompt + "&size=1024*1024&n=1");
            Request request = new Request.Builder()
                    .url("http://47.99.133.237:8080/imageCreation")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("cookie", token)
                    .build();

            client.newCall(request).enqueue(callback);
        });

        t.start();
    }
}
