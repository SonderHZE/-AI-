package com.example.mygptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.mygptapp.ViewModel.LoginViewModel;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;

public class MainActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginViewModel = ViewModelHolder.getInstance().getLoginViewModel();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁止横屏

        // 检查用户是否已经登录
        loginViewModel.autoLogin(this);

        Intent intent;
        if(loginViewModel.getUser().getValue().getUserID() == null) {
            Log.d("debug", "User not login");
            intent = new Intent(this, LoginActivity.class);
        } else {
            Log.d("debug", "User already login");
            intent = new Intent(this, ChatActivity.class);
        }
        startActivity(intent);

    }
}