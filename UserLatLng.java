package com.example.friendtracker.Model;

public class UserLatLng {
    double latitude, longitude;

    public UserLatLng(double latitude, double longitude) {
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
