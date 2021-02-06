package com.example.friendtracker.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.friendtracker.Activity.HomeActivity;
import com.example.friendtracker.Adapter.FriendListAdapter;
import com.example.friendtracker.Libs.GlobalDataService;
import com.example.friendtracker.Libs.ProgressDialogShow;
import com.example.friendtracker.Model.FriendDetails;
import com.example.friendtracker.Model.UserDetails;
import com.example.friendtracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.friendtracker.Libs.Params.FRIENDS;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment {

    View view;
    ArrayList<FriendDetails> friendDetailsList = new ArrayList<>();
    UserDetails userDetails;
    String currentUserId;
    private RecyclerView recyclerView;
    private TextView noFriendTv;
    private DatabaseReference databaseReference;
    private FriendListAdapter friendListAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_friend_list, container, false);
            findAllIds();
            databaseReference = FirebaseDatabase.getInstance().getReference(FRIENDS);
            userDetails = GlobalDataService.getInstance().getUserDetails(getActivity());
            currentUserId = userDetails.getUserId();
            linearLayoutManager = new LinearLayoutManager(getActivity());
            getFriendList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getFriendList() {
        try {
            ProgressDialogShow.openProgress(getActivity());
            databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        ProgressDialogShow.closeProgress();
                        friendDetailsList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            FriendDetails friendDetails = postSnapshot.getValue(FriendDetails.class);
                            friendDetailsList.add(friendDetails);
                        }
                        setData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    try {
                        ProgressDialogShow.closeProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        try {
            if (friendDetailsList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                noFriendTv.setVisibility(View.GONE);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                friendListAdapter = new FriendListAdapter(getActivity(), friendDetailsList, currentUserId);
                recyclerView.setAdapter(friendListAdapter);
                friendListAdapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
                noFriendTv.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findAllIds() {
        try {
            recyclerView = view.findViewById(R.id.recyclerView);
            noFriendTv = view.findViewById(R.id.noFriendTv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((HomeActivity) getActivity()).addFriendBtn.setVisibility(View.VISIBLE);
            ((HomeActivity) getActivity()).currentScreenName.setText("Friends");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            ((HomeActivity) getActivity()).addFriendBtn.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
