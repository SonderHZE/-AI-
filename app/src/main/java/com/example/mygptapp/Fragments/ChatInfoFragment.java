package com.example.mygptapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mygptapp.Adapter.ChatAdapter;
import com.example.mygptapp.Interfaces.ChatLIstItemClickListener;
import com.example.mygptapp.R;
import com.example.mygptapp.ViewModel.ChatViewModel;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;
import com.example.mygptapp.pojo.ChatInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class ChatInfoFragment extends Fragment {
    private ChatViewModel chatViewModel;
    private RecyclerView recyclerView;

    public ChatInfoFragment() {
    }

    public static ChatInfoFragment newInstance() {
        ChatInfoFragment fragment = new ChatInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat_info, container, false);
        recyclerView = view.findViewById(R.id.chat_info_recycler_view);
        chatViewModel = ViewModelHolder.getInstance().getChatViewModel();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ChatInfo> chatInfos;
        chatInfos = chatViewModel.getChatInfoList().getValue();

        ChatAdapter chatAdapter = new ChatAdapter(chatInfos, position -> startChatFragment(position));
        recyclerView.setAdapter(chatAdapter);

        ViewModelHolder.getInstance().getChatViewModel().getChatInfoList().observe(getViewLifecycleOwner(), chatInfos1 -> {
            ChatAdapter chatAdapter1 = new ChatAdapter(chatInfos1, position -> startChatFragment(position));
            recyclerView.setAdapter(chatAdapter1);
        });
        return view;
    }



    // 导航至聊天界面,
    void startChatFragment(int position) {
        ViewModelHolder.currentChatID = Integer.valueOf(ViewModelHolder.getInstance().getChatViewModel().getChatInfoList().getValue().get(position).getChatID());
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.nav_chat);

        ViewModelHolder.getInstance().getMessageViewModel().getChatInfo(ViewModelHolder.currentChatID, getContext());
    }
}