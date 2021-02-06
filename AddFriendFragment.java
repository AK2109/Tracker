package com.example.friendtracker.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.friendtracker.Activity.HomeActivity;
import com.example.friendtracker.Libs.GlobalDataService;
import com.example.friendtracker.Libs.ProgressDialogShow;
import com.example.friendtracker.Libs.Utils;
import com.example.friendtracker.Model.FriendDetails;
import com.example.friendtracker.Model.UserDetails;
import com.example.friendtracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
import static com.example.friendtracker.Libs.Params.FRIENDS;
import static com.example.friendtracker.Libs.Params.REGISTRATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendFragment extends Fragment {

    View view;
    FriendDetails friendDetails;
    View.OnClickListener outsideAreaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Utils.hideKeyboard(getActivity(), view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private UserDetails userDetails;
    private DatabaseReference databaseReference;
    private EditText uniqueKey, nickName;
    private TextInputLayout uniqueKeyError, nickNameError;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                removeAllError();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    View.OnClickListener addNowBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Utils.hideKeyboard(getActivity(), view);
                if (isAllValid()) {
                    ProgressDialogShow.openProgress(getActivity());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                ProgressDialogShow.closeProgress();
                                String uniqueKeyStr = uniqueKey.getText().toString().trim();
                                boolean isUniqueIdRegistered = false;
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    isUniqueIdRegistered = String.valueOf(postSnapshot.child("userId").getValue()).equals(uniqueKeyStr);
                                    if (isUniqueIdRegistered) {
                                        friendDetails = postSnapshot.getValue(FriendDetails.class);
                                        break;
                                    }
                                }
                                if (isUniqueIdRegistered && !uniqueKeyStr.equalsIgnoreCase(userDetails.getUserId())) {
                                    checkInFriendDataBase();
                                } else {
                                    Toast.makeText(getActivity(), "User does not exist.", Toast.LENGTH_SHORT).show();
                                }
                                removeAllFocus();
                                getActivity().onBackPressed();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            try {
                                Log.d(TAG, "onCancelled: called");
                                ProgressDialogShow.closeProgress();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        private void removeAllFocus() {
                            try {
                                uniqueKey.clearFocus();
                                nickName.clearFocus();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Button addNowBtn;
    private RelativeLayout outSideArea;

    private void checkInFriendDataBase() {
        try {
            String nickNameStr = nickName.getText().toString().trim();
            String userId = userDetails.getUserId();
            databaseReference = FirebaseDatabase.getInstance().getReference(FRIENDS);
            String friendId = databaseReference.push().getKey();
            friendDetails.setNickname(nickNameStr);
            friendDetails.setFriendId(friendId);
          //  Query query = databaseReference.child(userId).orderByChild("email").equalTo("Avisek11@gmail.com").limitToFirst(1);

           /* query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 8;
                    i++;
                    Log.d(TAG, "onDataChange: " + dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: called");
                }
            });*/
          //  Log.d(TAG, "checkInFriendDataBase: " + query);
            databaseReference.child(userId).child(friendId).setValue(friendDetails);
             databaseReference = FirebaseDatabase.getInstance().getReference(REGISTRATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_add_friend, container, false);
            findAllIds();
            userDetails = GlobalDataService.getInstance().getUserDetails(getActivity());
            databaseReference = FirebaseDatabase.getInstance().getReference(REGISTRATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void findAllIds() {
        try {
            uniqueKey = view.findViewById(R.id.uniqueKey);
            nickName = view.findViewById(R.id.nickName);
            outSideArea = view.findViewById(R.id.outSideArea);
            uniqueKeyError = view.findViewById(R.id.uniqueKeyError);
            nickNameError = view.findViewById(R.id.nickNameError);
            addNowBtn = view.findViewById(R.id.addNowBtn);

            outSideArea.setOnClickListener(outsideAreaClickListener);
            addNowBtn.setOnClickListener(addNowBtnListener);

            uniqueKey.addTextChangedListener(textWatcher);
            nickName.addTextChangedListener(textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAllValid() {
        String uniqueKeyStr = uniqueKey.getText().toString().trim();
        String nickNameStr = nickName.getText().toString().trim();
        if (uniqueKeyStr.isEmpty()) {
            uniqueKeyError.setError("Enter friend unique key");
            uniqueKey.requestFocus();
            return false;
        }
        if (nickNameStr.isEmpty()) {
            nickNameError.setError("Enter friend nickname");
            nickNameError.requestFocus();
            return false;
        } else if (nickNameStr.length() < 3) {
            nickNameError.setError("Nickname must be at least 3 character long");
            nickNameError.requestFocus();
            return false;
        }
        return true;
    }

    private void removeAllError() {
        try {
            uniqueKeyError.setError(null);
            nickNameError.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            ((HomeActivity) getActivity()).currentScreenName.setText("ADD FRIEND");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
