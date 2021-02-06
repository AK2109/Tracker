package com.example.friendtracker.Libs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;

public class ProgressDialogShow {
    private static ProgressDialog progressDialog;

    public static void openProgress(Context context) {
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading Please Wait ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    try {
                        closeProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeProgress() {
        try {
            if (progressDialog != null) {
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
