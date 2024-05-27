package com.example.mygptapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mygptapp.Adapter.MyArrayAdapter;
import com.example.mygptapp.R;
import com.example.mygptapp.ViewModel.LoginViewModel;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;
import com.example.mygptapp.pojo.MyResponse;
import com.example.mygptapp.pojo.UserEntity;


public class ToolFragment extends Fragment {

    private TextView userID, userName, userPhone, defaultSystem;
    private Spinner defaultModel;
    private Button updateBtn;

    public ToolFragment() {
        // Required empty public constructor
    }

    public static ToolFragment newInstance() {
        ToolFragment fragment = new ToolFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tool, container, false);

        userID = view.findViewById(R.id.user_id_value);
        userName = view.findViewById(R.id.username_value);
        userPhone = view.findViewById(R.id.phone_value);
        defaultModel = view.findViewById(R.id.default_model_spinner);
        defaultSystem = view.findViewById(R.id.default_prompt_value);


        //设置spinner的可选项为“通义千问”和“文心一言”
        defaultModel.setAdapter(new MyArrayAdapter(getContext(), R.layout.spinner_item, new String[]{"通义千问", "文心一言"}));

        LoginViewModel loginViewModel = ViewModelHolder.getInstance().getLoginViewModel();
        UserEntity user = loginViewModel.getUser().getValue();
        userID.setText(user.getUserID().toString());
        userName.setText(user.getUserName());
        userPhone.setText(user.getMobilePhone());
        defaultSystem.setText(user.getDefaultPrompt());

        // 设置spinner的默认选项为用户的默认模型
        if(user.getDefaultModel().equals("通义千问")) {
            defaultModel.setSelection(0);
        } else {
            defaultModel.setSelection(1);
        }

        updateBtn = view.findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(v -> {
            MyResponse res = ViewModelHolder.getInstance().getLoginViewModel().updateUserSettings(defaultModel.getSelectedItem().toString(), defaultSystem.getText().toString());
            if(res.getStatus().equals("success")) {
                ViewModelHolder.getInstance().getLoginViewModel().getUser().getValue().setDefaultModel(defaultModel.getSelectedItem().toString());
                Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}