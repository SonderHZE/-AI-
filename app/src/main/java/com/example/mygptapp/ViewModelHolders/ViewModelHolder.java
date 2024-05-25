package com.example.mygptapp.ViewModelHolders;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.mygptapp.ViewModel.ChatViewModel;
import com.example.mygptapp.ViewModel.LoginViewModel;
import com.example.mygptapp.ViewModel.MessageViewModel;

public class ViewModelHolder implements ViewModelStoreOwner {
    private final ViewModelStore viewModelStore;
    private static ViewModelHolder instance;
    private static LoginViewModel loginViewModel;
    private static ChatViewModel chatViewModel;
    private static MessageViewModel messageViewModel;
    public static Integer currentChatID = -1;

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    private ViewModelHolder() {
        viewModelStore = new ViewModelStore();
    }

    public static ViewModelHolder getInstance() {
        if(instance == null) {
            synchronized (ViewModelHolder.class){
                if(instance == null) {
                    instance = new ViewModelHolder();
                }
            }
        }
        return instance;
    }
    public LoginViewModel getLoginViewModel() {
        if(loginViewModel == null) {
            synchronized (ViewModelHolder.class){
                if(loginViewModel == null) {
                    loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
                }
            }
        }
        return loginViewModel;
    }

    public ChatViewModel getChatViewModel() {
        if(chatViewModel == null) {
            synchronized (ViewModelHolder.class){
                if(chatViewModel == null) {
                    chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
                }
            }
        }
        return chatViewModel;
    }

    public MessageViewModel getMessageViewModel() {
        if(messageViewModel == null) {
            synchronized (ViewModelHolder.class){
                if(messageViewModel == null) {
                    messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
                }
            }
        }
        return messageViewModel;
    }

    public void getAllChatList(Context context) throws InterruptedException {
        getChatViewModel().getAllChatInfo(context);
    }
}
