package com.example.mygptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.mygptapp.Adapter.ChatAdapter;
import com.example.mygptapp.Fragments.ChatFragment;
import com.example.mygptapp.Fragments.ChatInfoFragment;
import com.example.mygptapp.Fragments.ImageFragment;
import com.example.mygptapp.Fragments.ToolFragment;
import com.example.mygptapp.ViewModel.ChatViewModel;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatActivity extends AppCompatActivity {

    private ChatFragment chatFragment;
    private ToolFragment toolFragment;
    private Fragment activeFragment;
    private ChatViewModel chatViewModel;
    private ImageFragment imageFragment;
    private ChatInfoFragment chatInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁止横屏
        Log.d("debug", "ChatActivity onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        // 获取所有聊天信息
        chatViewModel = ViewModelHolder.getInstance().getChatViewModel();
        try {
            chatViewModel.getAllChatInfo(this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 初始化Fragment
        chatFragment = new ChatFragment();
        toolFragment = new ToolFragment();
        chatInfoFragment = new ChatInfoFragment();
        imageFragment = new ImageFragment();

        // 添加Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, toolFragment, "tool").hide(toolFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, chatFragment, "chat").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, chatInfoFragment, "chatInfo").hide(chatInfoFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, imageFragment, "image").hide(imageFragment).commit();

        activeFragment = chatFragment;

        // 导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_chat) {
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(chatFragment).commit();
                activeFragment = chatFragment;
            } else if(item.getItemId() == R.id.nav_tool) {
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(toolFragment).commit();
                activeFragment = toolFragment;
            } else if(item.getItemId() == R.id.nav_history){
                try {
                    ViewModelHolder.getInstance().getChatViewModel().getAllChatInfo(this);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(chatInfoFragment).commit();
                activeFragment = chatInfoFragment;
            }else if(item.getItemId() == R.id.nav_image){
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(imageFragment).commit();
                activeFragment = imageFragment;
            }
            return true;
        });

        // 默认选择第一个
        navView.setSelectedItemId(R.id.nav_chat);
    }
}