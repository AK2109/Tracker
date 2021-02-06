package com.example.friendtracker.Libs;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static boolean isEmailValid(String email) {
        try {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void hideKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void convertDateTime(String inputStr, String inputFormat, String outputFormat) {
        try {
        String outputStr =  "";
           // Date myDate = new Date();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
