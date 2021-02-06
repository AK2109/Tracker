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
import com.example.friendtracker.Model.UserDetails;
import com.example.friendtracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
import static com.example.friendtracker.Libs.Params.REGISTRATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {

    View view;
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
    private EditText password, confirmPassword;
    private TextInputLayout passwordError, confirmPasswordError;
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
    View.OnClickListener changePasswordBtnListener = new View.OnClickListener() {
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
                                String firstNameStr = ChangePasswordFragment.this.userDetails.getFirstName();
                                String lastNameStr = ChangePasswordFragment.this.userDetails.getLastName();
                                String emailStr = ChangePasswordFragment.this.userDetails.getEmail();
                                String mobileNoStr = ChangePasswordFragment.this.userDetails.getMobileNo();
                                String passwordStr = password.getText().toString().trim();
                                String userId = ChangePasswordFragment.this.userDetails.getUserId();
                                UserDetails userDetails = new UserDetails(userId, firstNameStr, lastNameStr, emailStr, mobileNoStr, passwordStr);
                                databaseReference.child(userId).setValue(userDetails);
                                GlobalDataService.getInstance().setUserDetails(getActivity(), userDetails);
                                Toast.makeText(getActivity(), "Password Changed Successfully.", Toast.LENGTH_SHORT).show();
                                removeAllFocus();
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
                                password.clearFocus();
                                confirmPassword.clearFocus();
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
    private Button changePasswordBtn;
    private RelativeLayout outSideArea;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_change_password, container, false);
            findAllIds();
            databaseReference = FirebaseDatabase.getInstance().getReference(REGISTRATION);
            userDetails = GlobalDataService.getInstance().getUserDetails(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void findAllIds() {
        try {
            password = view.findViewById(R.id.password);
            confirmPassword = view.findViewById(R.id.confirmPassword);
            outSideArea = view.findViewById(R.id.outSideArea);
            passwordError = view.findViewById(R.id.passwordError);
            confirmPasswordError = view.findViewById(R.id.confirmPasswordError);
            changePasswordBtn = view.findViewById(R.id.changePasswordBtn);

            outSideArea.setOnClickListener(outsideAreaClickListener);
            changePasswordBtn.setOnClickListener(changePasswordBtnListener);

            password.addTextChangedListener(textWatcher);
            confirmPassword.addTextChangedListener(textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAllValid() {
        String passwordStr = password.getText().toString().trim();
        String confirmPasswordStr = confirmPassword.getText().toString().trim();
        if (passwordStr.isEmpty()) {
            passwordError.setError("Enter your password");
            password.requestFocus();
            return false;
        } else if (passwordStr.length() < 6) {
            passwordError.setError("Password must be at least 6 character long");
            password.requestFocus();
            return false;
        }
        if (!confirmPasswordStr.equalsIgnoreCase(passwordStr)) {
            confirmPassword.setError("Confirm password does not match");
            confirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void removeAllError() {
        try {
            passwordError.setError(null);
            confirmPasswordError.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            ((HomeActivity) getActivity()).currentScreenName.setText("Change Password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
