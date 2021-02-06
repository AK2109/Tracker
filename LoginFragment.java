package com.example.friendtracker.Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import static com.example.friendtracker.Libs.Params.REGISTRATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    View view;
    View.OnClickListener signUpBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Utils.hideKeyboard(getActivity(), view);
                ((LoginActivity) getActivity()).pushFragments(new RegistrationFragment(), true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    // ...
    View.OnClickListener hideKeyboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Utils.hideKeyboard(getActivity(), view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener forgotPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            try {
                Utils.hideKeyboard(getActivity(), view);
                final Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.popup_forgot_password);
                dialog.show();
                final EditText email;
                final TextInputLayout emailError;
                Button cancelBtn, resetBtn;
                email = dialog.findViewById(R.id.email);
                emailError = dialog.findViewById(R.id.emailError);
                cancelBtn = dialog.findViewById(R.id.cancelBtn);
                resetBtn = dialog.findViewById(R.id.resetBtn);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dialog.cancel();
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                resetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String emailStr = email.getText().toString().trim();
                            boolean isEmailValid = true;
                            if (emailStr.isEmpty()) {
                                isEmailValid = false;
                                emailError.setError("Enter your email");
                                email.requestFocus();
                            } else if (!Utils.isEmailValid(emailStr)) {
                                isEmailValid = false;
                                emailError.setError("Enter valid email");
                                email.requestFocus();
                            }
                            if (isEmailValid) {
                                dialog.cancel();
                                dialog.dismiss();
                                Utils.displayToast(getActivity(), "Reset password link has been sent to your link.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private DatabaseReference databaseReference;
    private EditText email, password;
    private TextInputLayout emailError, passwordError;

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
    View.OnClickListener signInBtnListener = new View.OnClickListener() {
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
                                String emailStr = email.getText().toString().trim();
                                String passwordStr = password.getText().toString().trim();
                                boolean isEmailPresent = false, isPasswordPresent = false, isSuccessfulLogin = false;
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    isEmailPresent = String.valueOf(postSnapshot.child("email").getValue()).equals(emailStr);
                                    isPasswordPresent = String.valueOf(postSnapshot.child("password").getValue()).equals(passwordStr);
                                    if (isEmailPresent && isPasswordPresent) {
                                        isSuccessfulLogin = true;
                                        UserDetails userDetails = postSnapshot.getValue(UserDetails.class);
                                        GlobalDataService.getInstance().setUserDetails(getActivity(), userDetails);
                                        break;
                                    }
                                    isEmailPresent = false;
                                    isPasswordPresent = false;
                                }
                                if (isSuccessfulLogin) {
                                    Toast.makeText(getActivity(), "Login Successful.", Toast.LENGTH_LONG).show();
                                    //  UserDetails user = new UserDetails(userId,firstNameStr, lastNameStr, emailStr, mobileNoStr, passwordStr);
                                    //   databaseReference.child(userId).setValue(user);
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Invalid Credential. Please Check.", Toast.LENGTH_SHORT).show();
                                }
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private RelativeLayout outSideArea;
    private Button signInBtn;
    private TextView signUpBtn, forgotPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_login, container, false);
            findAllIds();
            databaseReference = FirebaseDatabase.getInstance().getReference(REGISTRATION);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void findAllIds() {
        try {
            email = view.findViewById(R.id.email);
            password = view.findViewById(R.id.password);
            emailError = view.findViewById(R.id.emailError);
            passwordError = view.findViewById(R.id.passwordError);
            signInBtn = view.findViewById(R.id.signInBtn);
            signUpBtn = view.findViewById(R.id.signUpBtn);
            outSideArea = view.findViewById(R.id.outSideArea);
            forgotPassword = view.findViewById(R.id.forgotPassword);

            signUpBtn.setOnClickListener(signUpBtnListener);
            signInBtn.setOnClickListener(signInBtnListener);
            forgotPassword.setOnClickListener(forgotPasswordListener);
            outSideArea.setOnClickListener(hideKeyboardListener);
            email.addTextChangedListener(textWatcher);
            password.addTextChangedListener(textWatcher);
            email.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    email.setFocusable(true);
                    email.setFocusableInTouchMode(true);
                    return false;
                }
            });
            password.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    password.setFocusable(true);
                    password.setFocusableInTouchMode(true);
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAllValid() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        if (emailStr.isEmpty()) {
            emailError.setError("Enter your email");
            email.requestFocus();
            return false;
        } else if (!Utils.isEmailValid(emailStr)) {
            emailError.setError("Enter valid email");
            email.requestFocus();
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
        return true;
    }

    private void removeAllError() {
        try {
            emailError.setError(null);
            passwordError.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            ((LoginActivity) getActivity()).currentScreenName.setText("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
