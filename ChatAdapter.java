package com.example.friendtracker.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friendtracker.Fragment.ChatFragment;
import com.example.friendtracker.Model.ChatModel;
import com.example.friendtracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ObjectViewHolder> {

    ChatFragment chatFragment;
    List<ChatModel> chatList;
    String currentUserId, friendUserId;

    public ChatAdapter(ChatFragment chatFragment, List<ChatModel> chatList) {
        this.chatFragment = chatFragment;
        this.chatList = chatList;
    }

    public void setUserId(String currentUserId, String friendUserId) {
        try {
            this.currentUserId = currentUserId;
            this.friendUserId = friendUserId;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(chatFragment.getContext()).inflate(R.layout.row_chat, viewGroup, false);
        return new ObjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjectViewHolder holder, int position) {
        try {
            holder.setAllData(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ObjectViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout receiverView, senderView;
        TextView receiverText, receiverTime, senderText, senderTime;

        public ObjectViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverView = itemView.findViewById(R.id.receiverView);
            senderView = itemView.findViewById(R.id.senderView);
            receiverText = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            senderText = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }

        public void setAllData(int position) {
            try {
                String senderId = chatList.get(position).getSenderId();
                String receiverId = chatList.get(position).getReceiverId();
                String messageText = chatList.get(position).getMessageText();
                long timeStamp = chatList.get(position).getTimeStamp();

                String timeStr = new SimpleDateFormat("HH:mm").format(new Date(timeStamp));

                if (currentUserId.equals(senderId) && friendUserId.equals(receiverId)) {
                    senderView.setVisibility(View.VISIBLE);
                    receiverView.setVisibility(View.GONE);
                    senderText.setText(messageText);
                    senderTime.setText(timeStr);
                } else if (currentUserId.equals(receiverId) && friendUserId.equals(senderId)) {
                    receiverView.setVisibility(View.VISIBLE);
                    senderView.setVisibility(View.GONE);
                    receiverText.setText(messageText);
                    receiverTime.setText(timeStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
