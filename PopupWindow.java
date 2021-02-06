package com.example.friendtracker.Libs;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendtracker.Activity.HomeActivity;
import com.example.friendtracker.Activity.LoginActivity;
import com.example.friendtracker.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static com.example.friendtracker.Libs.Params.FRIENDS;

public class PopupWindow {

    public static void showLogoutPopup(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_logout);
        dialog.show();
        TextView noBtn = dialog.findViewById(R.id.noBtn);
        TextView yesBtn = dialog.findViewById(R.id.yesBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
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
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GlobalDataService.getInstance().removeCurrentUser(activity);
                    dialog.cancel();
                    dialog.dismiss();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showKeyPopup(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_key);
        dialog.show();
        TextView myKeyTv = dialog.findViewById(R.id.myKeyTv);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView copyBtn = dialog.findViewById(R.id.copyBtn);
        final String keyStr = GlobalDataService.getInstance().getUserDetails(activity).getUserId();
        myKeyTv.setText(keyStr);
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
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("copied text", keyStr);
                    clipboard.setPrimaryClip(clip);
                    dialog.cancel();
                    dialog.dismiss();
                    Toast.makeText(activity, "Your Key Copied.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showHelpPopup(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_help);
        dialog.show();
        TextView myKeyTv = dialog.findViewById(R.id.myKeyTv);
        TextView cancelBtn = dialog.findViewById(R.id.cancelBtn);
        TextView okBtn = dialog.findViewById(R.id.okBtn);
        final String keyStr = GlobalDataService.getInstance().getUserDetails(activity).getUserId();
        myKeyTv.setText(keyStr);
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
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.cancel();
                    dialog.dismiss();
                    createNotificationChannel(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void createNotificationChannel(Activity activity) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

       /* Intent snoozeIntent = new Intent(activity, HomeActivity.class);
        snoozeIntent.setAction("action");
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(activity, 0, snoozeIntent, 0);
        int CHANNEL_ID =1;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_snooze, getString(R.string.snooze),
                        snoozePendingIntent);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "name";
                String description = "description";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Notification Title")
                        .setContentText("Notification Text")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
            }*/

    }

    public static void removeFriendPopup(final Context context, final String friendId, final String currentUserId) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_remove_friend);
        dialog.show();
        TextView noBtn = dialog.findViewById(R.id.noBtn);
        TextView yesBtn = dialog.findViewById(R.id.yesBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
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
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.cancel();
                    dialog.dismiss();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FRIENDS);
                    databaseReference.child(currentUserId).child(friendId).removeValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
