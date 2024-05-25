package com.example.mygptapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygptapp.R;
import com.example.mygptapp.pojo.Message;

import java.util.List;

import io.noties.markwon.Markwon;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Message> list;
    private final Markwon markwon;
    public MsgAdapter(List<Message> list, Context context){
        this.list = list;
        markwon = Markwon.create(context);
    }

    public void updateData(List<Message> messages) {
        this.list = messages;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        TextView left_msg;

        LinearLayout rightLayout;
        TextView right_msg;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            left_msg = view.findViewById(R.id.left_msg);

            rightLayout = view.findViewById(R.id.right_layout);
            right_msg = view.findViewById(R.id.right_msg);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = list.get(position);
        String content = msg.getContent().replace("\\n", "\n");
        if(msg.getType() == "assistant"){
            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.left_msg.setText(msg.getContent());
            markwon.setMarkdown(holder.left_msg, content);

            holder.rightLayout.setVisibility(View.GONE);
        }else if(msg.getType() == "user"){
            //如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.right_msg.setText(msg.getContent());
            markwon.setMarkdown(holder.right_msg, content);

            holder.leftLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
