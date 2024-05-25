package com.example.mygptapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygptapp.Interfaces.ChatLIstItemClickListener;
import com.example.mygptapp.R;
import com.example.mygptapp.pojo.ChatInfo;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private List<ChatInfo> chatList;
    private ChatLIstItemClickListener myListener;

    public void setChatList(List<ChatInfo> chatList) {
        this.chatList = chatList;
    }

    public void setMyListener(ChatLIstItemClickListener myListener) {
        this.myListener = myListener;
    }

    public ChatAdapter(List<ChatInfo> chatList, ChatLIstItemClickListener myListener){
        this.chatList = chatList;
        this.myListener = myListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView chatTitle;
        TextView chatTime;

        public ViewHolder(View view){
            super(view);
            chatTitle = view.findViewById(R.id.chat_title);
            chatTime = view.findViewById(R.id.chat_time);
        }
    }


    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatInfo chatInfo = chatList.get(position);
        if(chatInfo != null) {
            holder.chatTitle.setText(chatInfo.getChatTitle());
            holder.chatTime.setText(chatInfo.getTime());
            holder.itemView.setOnClickListener(v -> {
                myListener.onChatListItemClick(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
