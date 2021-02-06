package com.example.friendtracker.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friendtracker.Activity.HomeActivity;
import com.example.friendtracker.Fragment.FriendDetailsPage;
import com.example.friendtracker.Libs.PopupWindow;
import com.example.friendtracker.Model.FriendDetails;
import com.example.friendtracker.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.friendtracker.Libs.Params.FRIENDS;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ObjectViewHolder> {

    Context context;
    ArrayList<FriendDetails> friendDetailsList;
    private String currentUserId;

    public FriendListAdapter(Context context, ArrayList<FriendDetails> friendDetailsList, String currentUserId) {
        this.context = context;
        this.friendDetailsList = friendDetailsList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_friend_details, viewGroup, false);
        return new ObjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjectViewHolder holder, final int position) {
        try {
            String fullNameStr = friendDetailsList.get(position).getNickname();
            holder.friendFullName.setText(fullNameStr);
            holder.removeFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String friendId = friendDetailsList.get(position).getFriendId();
                        PopupWindow.removeFriendPopup(context,friendId,currentUserId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FriendDetails friendDetails = friendDetailsList.get(position);
                        FriendDetailsPage friendDetailsPage = new FriendDetailsPage();
                        friendDetailsPage.setFriendDetails(friendDetails);
                        ((HomeActivity) context).pushFragments(friendDetailsPage, true, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return friendDetailsList.size();
    }

    class ObjectViewHolder extends RecyclerView.ViewHolder {
        TextView friendFullName;
        RelativeLayout removeFriendBtn;


        public ObjectViewHolder(@NonNull View itemView) {
            super(itemView);
            friendFullName = itemView.findViewById(R.id.friendFullName);
            removeFriendBtn = itemView.findViewById(R.id.removeFriendBtn);
        }
    }
}
