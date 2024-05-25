package com.example.mygptapp.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.mygptapp.Database.AppDatabase;
import com.example.mygptapp.Fragments.ChatFragment;
import com.example.mygptapp.Uils.JsonUtil;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;
import com.example.mygptapp.pojo.ChatInfo;
import com.example.mygptapp.pojo.Message;
import com.example.mygptapp.pojo.MyResponse;
import com.example.mygptapp.pojo.UserEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

public class MessageViewModel extends ViewModel {
    private MutableLiveData<List<Message>> msgList;
    private ChatInfo chatInfo;

    public MessageViewModel() {
        msgList = new MutableLiveData<>();
        msgList.setValue(new ArrayList<>());
        chatInfo = new ChatInfo();
    }

    public MutableLiveData<List<Message>> getMsgList() {
        return msgList;
    }

    public void getChatInfo(Integer currentChatID, Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "user").build();
        Thread t = new Thread(() -> {
            // 清空msgList
            msgList.getValue().clear();

            String token = db.userDao().getToken();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://47.99.133.237:8080/getChatInfo?chatID=" + currentChatID)
                    .get()
                    .addHeader("cookie", token)
                    .build();

            // 发送同步请求
            try {
                okhttp3.Response response = client.newCall(request).execute();
                String res = response.body().string();
                Log.d("getChatInfo", res);
                MyResponse myResponse = JsonUtil.parseJson(res);
                if(myResponse.getStatus().equals("success")) {
                   StringBuilder sb;
                   sb = new StringBuilder(myResponse.getData().getAsString());

                    // 找到第一个user:的位置
                    int userIndex = sb.indexOf("user:");
                    // 得到user:之前的内容，删除system:以及换行符
                    String system = sb.substring(0, userIndex).replace("system:", "").replace("\n", "");
                    chatInfo.setSystem(system);

                    //按照user:以及assistant:分割字符串
                    String[] split = sb.substring(userIndex).split("user:");

                    // 遍历splitArray，将每一项按照assistant分割
                    for (String s : split) {
                        if(s.equals("")) {
                            continue;
                        }
                        String[] split1 = s.split("assistant:");
                        // 如果split1的长度为1，说明没有assistant，只有user
                        if (split1.length == 1) {
                            // 将system和user添加到msgList中
                            msgList.getValue().add(new Message(system, split1[0], "user"));
                        } else {
                            // 将system和assistant添加到msgList中，删除最后面的换行符
                            split1[0] = split1[0].substring(0, split1[0].length() - 1);
                            split1[1] = split1[1].substring(0, split1[1].length() - 1);

                            msgList.getValue().add(new Message(system, split1[0], "user"));
                            msgList.getValue().add(new Message(system, split1[1], "assistant"));
                        }
                    }

                    // 更新msgList
                    msgList.postValue(msgList.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public void sendMessage(String content, Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "user").build();
        Thread t = new Thread(() -> {
            UserEntity user = ViewModelHolder.getInstance().getLoginViewModel().getUser().getValue();
            String token = db.userDao().getToken();
            chatInfo.setChatID(ViewModelHolder.currentChatID.toString());
            if(chatInfo.getChatID().equals("-1")) {
                chatInfo.setSystem(user.getDefaultPrompt());
            }

            msgList.getValue().add(new Message(chatInfo.getSystem(), content, "user"));

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://47.99.133.237:8080/aiChat?inputValue=" + content +
                            "&chatID=" +  chatInfo.getChatID()+
                            "&chatModel=" + user.getDefaultModel() +
                            "&temperature=0.85" +
                            "&top_p=0.8" +
                            "&system=" + chatInfo.getSystem())
                    .get()
                    .addHeader("cookie", token)
                    .build();


            EventSourceListener listener = new EventSourceListener() {
                int[] count = {0};
                boolean ifClose = false;
                @Override
                public void onOpen(EventSource eventSource, okhttp3.Response response) {
                    super.onOpen(eventSource, response);
                    // 连接已打开
                }

                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    super.onEvent(eventSource, id, type, data);
                    if(ifClose) {
                        // 获取ID
                        ViewModelHolder.currentChatID = Integer.valueOf(data);
                        return;
                    }

                    // 收到事件
                    if (data.equals("CHAT COMPLETED!")) {
                        ifClose = true;
                        return;
                    }
                    if (count[0] == 0) {
                        msgList.getValue().add(new Message(chatInfo.getSystem(), data, "assistant"));
                        count[0]++;
                    } else {
                        msgList.getValue().get(msgList.getValue().size() - 1).setContent(msgList.getValue().get(msgList.getValue().size() - 1).getContent() + data);
                    }
                    msgList.postValue(msgList.getValue());
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    super.onClosed(eventSource);
                    // 连接已关闭
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, okhttp3.Response response) {
                    super.onFailure(eventSource, t, response);
                    // 连接失败
                }
            };

            EventSource eventSource = EventSources.createFactory(client).newEventSource(request, listener);
        });

        t.start();

    }
}
