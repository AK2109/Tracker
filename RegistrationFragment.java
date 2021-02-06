package com.example.friendtracker.Fragment;


import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendtracker.Activity.HomeActivity;
import com.example.friendtracker.Activity.LoginActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {

    View view;
    View.OnClickListener signInBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Utils.hideKeyboard(getActivity(), view);
                ((LoginActivity) getActivity()).pushFragments(new LoginFragment(), false, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
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
    private DatabaseReference databaseReference;
    private EditText firstName, lastName, email, mobileNo, password, confirmPassword;
    private TextInputLayout firstNameError, lastNameError, emailError, mobileNoError, passwordError, confirmPasswordError;
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
    View.OnClickListener signUpBtnListener = new View.OnClickListener() {
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
                                String passwordStr = password.getText().toString().trim();
                                boolean isUserRegistered = false;
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    isUserRegistered = String.valueOf(postSnapshot.child("email").getValue()).equals(emailStr);
                                    if (isUserRegistered) {
                                        break;
                                    }
                                }
                                if (!isUserRegistered) {
                                    String userId = databaseReference.push().getKey();
                                    UserDetails userDetails = new UserDetails(userId,firstNameStr, lastNameStr, emailStr, mobileNoStr, passwordStr);
                                    databaseReference.child(userId).setValue(userDetails);
                                    GlobalDataService.getInstance().setUserDetails(getActivity(), userDetails);
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "UserDetails Already Registered. Login Now or create different UserDetails.", Toast.LENGTH_SHORT).show();
                                }
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
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private RelativeLayout outSideArea;
    private TextView signInBtn;
    private Button signUpBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_registration, container, false);
            findAllIds();
            firstName.setText("Abcd");
            lastName.setText("Mkjhg");
            email.setText("abcdmk1@gmail.com");
            password.setText("123456");
            confirmPassword.setText("123456");
            mobileNo.setText("5210008621");
            databaseReference = FirebaseDatabase.getInstance().getReference("registration");
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void findAllIds() {
        try {
            firstName = view.findViewById(R.id.firstName);
            lastName = view.findViewById(R.id.lastName);
            email = view.findViewById(R.id.email);
            mobileNo = view.findViewById(R.id.mobileNo);
            password = view.findViewById(R.id.password);
            confirmPassword = view.findViewById(R.id.confirmPassword);
            firstNameError = view.findViewById(R.id.firstNameError);
            lastNameError = view.findViewById(R.id.lastNameError);
            emailError = view.findViewById(R.id.emailError);
            mobileNoError = view.findViewById(R.id.mobileNoError);
            passwordError = view.findViewById(R.id.passwordError);
            confirmPasswordError = view.findViewById(R.id.confirmPasswordError);
            signInBtn = view.findViewById(R.id.signInBtn);
            signUpBtn = view.findViewById(R.id.signUpBtn);
            outSideArea = view.findViewById(R.id.outSideArea);

            signUpBtn.setOnClickListener(signUpBtnListener);
            signInBtn.setOnClickListener(signInBtnListener);
            outSideArea.setOnClickListener(outsideAreaClickListener);
            firstName.addTextChangedListener(textWatcher);
            lastName.addTextChangedListener(textWatcher);
            email.addTextChangedListener(textWatcher);
            mobileNo.addTextChangedListener(textWatcher);
            password.addTextChangedListener(textWatcher);
            confirmPassword.addTextChangedListener(textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAllValid() {
        String firstNameStr = firstName.getText().toString().trim();
        String lastNameStr = lastName.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String mobileNoStr = mobileNo.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        String confirmPasswordStr = confirmPassword.getText().toString().trim();
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
            firstNameError.setError(null);
            lastNameError.setError(null);
            emailError.setError(null);
            mobileNoError.setError(null);
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
            ((LoginActivity) getActivity()).currentScreenName.setText("Registration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
