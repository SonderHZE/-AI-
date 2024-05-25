package com.example.mygptapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mygptapp.ViewModel.LoginViewModel;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton, registerButton;
    private EditText userNameEdit;
    private EditText passwordEdit;
    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁止横屏
        Log.d("debug", "LoginActivity onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acticity);
        loginViewModel = ViewModelHolder.getInstance().getLoginViewModel();

        loginButton = findViewById(R.id.login_button);
        userNameEdit = findViewById(R.id.username_text);
        passwordEdit = findViewById(R.id.password_text);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v-> loginViewModel.login(userNameEdit.getText().toString(), passwordEdit.getText().toString(), this));
        registerButton.setOnClickListener(v-> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginViewModel.getError().observe(this, error -> Toast.makeText((Context) this, (CharSequence) error, Toast.LENGTH_SHORT).show());

        loginViewModel.getUser().observe(this, user -> {
            // 跳转到主页面
            if(user.getUserID() == null) {
                return;
            }

            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }
}