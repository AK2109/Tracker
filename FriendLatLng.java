package com.example.friendtracker.Model;

public class FriendLatLng {
    double latitude, longitude;

    public FriendLatLng() {
    }

    public FriendLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
