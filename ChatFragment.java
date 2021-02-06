package com.example.friendtracker.Fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.friendtracker.Adapter.ChatAdapter;
import com.example.friendtracker.Libs.GlobalDataService;
import com.example.friendtracker.Libs.ProgressDialogShow;
import com.example.friendtracker.Model.ChatModel;
import com.example.friendtracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    View view;
    RecyclerView chatRecyclerView;
    private TextView noChat;
    private EditText etMessage;
    private ImageView sendBtn;
    private DatabaseReference databaseReference;
    LinearLayoutManager linearLayoutManager;
    ChatAdapter chatAdapter;
    ChatFragment chatFragment;
    List<ChatModel> chatList = new ArrayList<>();
    String receiverId = "";
    String senderId = "";
    String uniquePairId = "";

    public void setreceiverId(String receiverId) {
        try {
            this.receiverId = receiverId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
            chatFragment = this;
            senderId = GlobalDataService.getInstance().getUserDetails(getActivity()).getUserId();
            findAllIds();
            createUniquePairId();
            String path = "chats/" + uniquePairId;
            databaseReference = FirebaseDatabase.getInstance().getReference(path);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            chatAdapter = new ChatAdapter(chatFragment, chatList);
            getChatList();
            //    setChatList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getChatList() {
        try {
            ProgressDialogShow.openProgress(getActivity());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProgressDialogShow.closeProgress();
                    chatList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ChatModel chatModel = postSnapshot.getValue(ChatModel.class);
                        chatList.add(chatModel);
                    }
                    if (chatList.size()>0){
                        noChat.setVisibility(View.GONE);
                        setChatList();
                    }else {
                        noChat.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createUniquePairId() {
        try {
            if (senderId.compareTo(receiverId) > 0) {
                uniquePairId = senderId + receiverId;
            } else {
                uniquePairId = receiverId + senderId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setChatList() {
        try {
            chatRecyclerView.setLayoutManager(linearLayoutManager);
            chatRecyclerView.setHasFixedSize(true);
            chatAdapter.setUserId(senderId,receiverId);
            chatRecyclerView.setAdapter(chatAdapter);
            chatRecyclerView.scrollToPosition(chatList.size() -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void findAllIds() {
        try {
            chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
            noChat = view.findViewById(R.id.noChat);
            etMessage = view.findViewById(R.id.etMessage);
            sendBtn = view.findViewById(R.id.sendBtn);

            sendBtn.setImageAlpha(30);
            sendBtn.setOnClickListener(sendBtnListener);
            etMessage.addTextChangedListener(etMessageChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextWatcher etMessageChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                chatRecyclerView.scrollToPosition(chatList.size() -1);
                if (s.toString().trim().length() > 0) {
                    sendBtn.setImageAlpha(255);
                } else {
                    sendBtn.setImageAlpha(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String chatMessage = etMessage.getText().toString().trim();
                if (!chatMessage.isEmpty()) {
                    // set to firebase and list
                    String chatId = databaseReference.push().getKey();
                    long currentTime = System.currentTimeMillis();
                    ChatModel chatModel = new ChatModel(chatMessage, receiverId, senderId, currentTime);
                    databaseReference.child(chatId).setValue(chatModel);
                    etMessage.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
