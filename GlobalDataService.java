package com.example.friendtracker.Libs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.friendtracker.Model.UserDetails;
import com.google.gson.Gson;

public class GlobalDataService {
    private static GlobalDataService mInstance = null;
    private double myLatitude, myLongitude, friendLatitude, friendLongitude;

    public static GlobalDataService getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalDataService();
        }
        return mInstance;
    }

    public static GlobalDataService getmInstance() {
        return mInstance;
    }

    public static void setmInstance(GlobalDataService mInstance) {
        GlobalDataService.mInstance = mInstance;
    }

    public void setUserDetails(Context context, UserDetails userDetails) {
        try {
            SharedPreferences pref = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Gson gson = new Gson();
            String jsonObject = gson.toJson(userDetails);
            editor.putString("userDetails", jsonObject);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserDetails getUserDetails(Context context) {
        UserDetails userDetails = null;
        try {
            SharedPreferences pref = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
            String json = pref.getString("userDetails", null);
            Gson gson = new Gson();
            userDetails = gson.fromJson(json, UserDetails.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }

    public void removeCurrentUser(Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Gson gson = new Gson();
            String jsonObject = gson.toJson(null);
            editor.putString("userDetails", jsonObject);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getMyLatitude() {
        return myLatitude;
    }

    public void setMyLatitude(double myLatitude) {
        this.myLatitude = myLatitude;
    }

    public double getMyLongitude() {
        return myLongitude;
    }

    public void setMyLongitude(double myLongitude) {
        this.myLongitude = myLongitude;
    }

    public double getFriendLatitude() {
        return friendLatitude;
    }

    public void setFriendLatitude(double friendLatitude) {
        this.friendLatitude = friendLatitude;
    }

    public double getFriendLongitude() {
        return friendLongitude;
    }

    public void setFriendLongitude(double friendLongitude) {
        this.friendLongitude = friendLongitude;
    }
}
