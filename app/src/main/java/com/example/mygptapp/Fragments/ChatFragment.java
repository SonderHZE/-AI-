package com.example.mygptapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mygptapp.Adapter.MsgAdapter;
import com.example.mygptapp.R;
import com.example.mygptapp.ViewModel.MessageViewModel;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;
import com.example.mygptapp.pojo.Message;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private MessageViewModel messageViewModel;
    private RecyclerView recyclerView;
    private EditText editText;
    private Button newChat;
    private List<Message> msgList;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        msgList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.message_area);
        editText = view.findViewById(R.id.input_area);
        newChat = view.findViewById(R.id.new_chat);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        msgList = new ArrayList<>();
        MsgAdapter adapter = new MsgAdapter(msgList, getContext());
        recyclerView.setAdapter(adapter);

        messageViewModel = ViewModelHolder.getInstance().getMessageViewModel();
        messageViewModel.getMsgList().observe(getViewLifecycleOwner(), messages -> {
            msgList.clear();
            msgList.addAll(messages);
            adapter.updateData(msgList);
            if (msgList.size() > 0) {
                recyclerView.smoothScrollToPosition(msgList.size() - 1);
            }
        });

        newChat.setOnClickListener(v -> {
            ViewModelHolder.currentChatID = -1;
            messageViewModel.getMsgList().getValue().clear();
            messageViewModel.getMsgList().setValue(new ArrayList<>());
            msgList.clear();

            adapter.updateData(msgList);
        });

        editText.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });

        return view;
    }

    void sendMessage() {
        String content = editText.getText().toString();
        if (content.equals("")) {
            Toast.makeText(getContext(), "消息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        messageViewModel.sendMessage(content, getContext());
        editText.setText("");
    }
}