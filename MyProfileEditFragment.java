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
import com.example.friendtracker.Libs.ProgressDialogShow;
import com.example.friendtracker.Libs.Utils;
import com.example.friendtracker.Model.UserDetails;
import com.example.friendtracker.R;
import com.example.friendtracker.Libs.GlobalDataService;
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
public class MyProfileEditFragment extends Fragment {

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
    private RelativeLayout outSideArea;
    private DatabaseReference databaseReference;
    private EditText firstName, lastName, email, mobileNo;
    private UserDetails userDetails;
    private TextInputLayout firstNameError, lastNameError, emailError, mobileNoError;
    View.OnClickListener updateProfileBtnListener = new View.OnClickListener() {
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
                                String firstNameStr = firstName.getText().toString().trim();
                                String lastNameStr = lastName.getText().toString().trim();
                                String emailStr = email.getText().toString().trim();
                                String mobileNoStr = mobileNo.getText().toString().trim();
                                String passwordStr = userDetails.getPassword();
                                boolean isEmailRegistered = false;
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    isEmailRegistered = String.valueOf(postSnapshot.child("email").getValue()).equals(emailStr);
                                    if (isEmailRegistered) {
                                        break;
                                    }
                                }
                                if (!isEmailRegistered || emailStr.equalsIgnoreCase(userDetails.getEmail())) {
                                    String userId = MyProfileEditFragment.this.userDetails.getUserId();
                                    UserDetails userDetails = new UserDetails(userId, firstNameStr, lastNameStr, emailStr, mobileNoStr,passwordStr);
                                    databaseReference.child(userId).setValue(userDetails);
                                    GlobalDataService.getInstance().setUserDetails(getActivity(), userDetails);
                                    Toast.makeText(getActivity(), "Profile Updated Successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Email address registered with another userDetails.", Toast.LENGTH_SHORT).show();
                                }
                                removeAllFocus();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        private void removeAllFocus() {
                            try {
                                firstName.clearFocus();
                                lastName.clearFocus();
                                email.clearFocus();
                                mobileNo.clearFocus();
                            }catch (Exception e){
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
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
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
    private Button updateProfileBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_my_profile_edit, container, false);
            findAllIds();
            setUserDetails();
            databaseReference = FirebaseDatabase.getInstance().getReference(REGISTRATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void setUserDetails() {
        try {
          userDetails = GlobalDataService.getInstance().getUserDetails(getActivity());
          String firstNamestr = userDetails.getFirstName();
          String lastNameStr = userDetails.getLastName();
          String emailStr = userDetails.getEmail();
          String mobileNoNumberStr = userDetails.getMobileNo();
          firstName.setText(firstNamestr);
          lastName.setText(lastNameStr);
          email.setText(emailStr);
          mobileNo.setText(mobileNoNumberStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findAllIds() {
        try {
            firstName = view.findViewById(R.id.firstName);
            lastName = view.findViewById(R.id.lastName);
            email = view.findViewById(R.id.email);
            mobileNo = view.findViewById(R.id.mobileNo);
            firstNameError = view.findViewById(R.id.firstNameError);
            lastNameError = view.findViewById(R.id.lastNameError);
            emailError = view.findViewById(R.id.emailError);
            mobileNoError = view.findViewById(R.id.mobileNoError);
            updateProfileBtn = view.findViewById(R.id.updateProfileBtn);
            outSideArea = view.findViewById(R.id.outSideArea);

            outSideArea.setOnClickListener(outsideAreaClickListener);
            updateProfileBtn.setOnClickListener(updateProfileBtnListener);

            firstName.addTextChangedListener(textWatcher);
            lastName.addTextChangedListener(textWatcher);
            email.addTextChangedListener(textWatcher);
            mobileNo.addTextChangedListener(textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAllValid() {
        String firstNameStr = firstName.getText().toString().trim();
        String lastNameStr = lastName.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String mobileNoStr = mobileNo.getText().toString().trim();
        if (firstNameStr.isEmpty()) {
            firstNameError.setError("Enter your first name");
            firstName.requestFocus();
            return false;
        }
        if (lastNameStr.isEmpty()) {
            lastNameError.setError("Enter your last name");
            lastName.requestFocus();
            return false;
        }
        if (emailStr.isEmpty()) {
            emailError.setError("Enter your email");
            email.requestFocus();
            return false;
        } else if (!Utils.isEmailValid(emailStr)) {
            emailError.setError("Enter valid email");
            email.requestFocus();
            return false;
        }
        if (mobileNoStr.isEmpty()) {
            mobileNoError.setError("Enter your mobile number");
            mobileNo.requestFocus();
            return false;
        } else if (mobileNoStr.length() < 10) {
            mobileNoError.setError("Enter valid mobile number");
            mobileNo.requestFocus();
            return false;
        }
        return true;
    }

    private void removeAllError() {
        try {
            firstNameError.setError(null);
            lastNameError.setError(null);
            emailError.setError(null);
            mobileNoError.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((HomeActivity) getActivity()).currentScreenName.setText("Edit Profile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
