package com.example.mygptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygptapp.ViewModelHolders.ViewModelHolder;
import com.example.mygptapp.pojo.MyResponse;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private TextView username, password, mobilePhone, password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁止横屏

        registerButton = findViewById(R.id.register_button);
        username = findViewById(R.id.username_text);
        password = findViewById(R.id.password_text);
        mobilePhone = findViewById(R.id.phone_text);
        password2 = findViewById(R.id.confirm_password_text);

        registerButton.setOnClickListener(v -> tryRegister());
    }

    void tryRegister() {
        //1. 校验完整性
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        String password2 = this.password2.getText().toString();
        String mobilePhone = this.mobilePhone.getText().toString();

        if (username.isEmpty() || password.isEmpty() || password2.isEmpty() || mobilePhone.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        //2. 校验密码一致性
        if (!password.equals(password2)) {
            Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        //3. 校验手机号格式
        if (mobilePhone.length() != 11) {
            Toast.makeText(RegisterActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
            return;
        }

        MyResponse res = ViewModelHolder.getInstance().getLoginViewModel().register(username, password, mobilePhone);
        if(res.getStatus().equals("success")) {
            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}