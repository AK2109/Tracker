package com.example.friendtracker.Model;

public class UserDetails {
    String firstName, lastName, email, mobileNo, password, userId, nickName;

    public UserDetails(String userId, String firstName, String lastName, String email, String mobileNo, String password, String nickName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.nickName = nickName;
    }

    public UserDetails(String userId, String firstName, String lastName, String email, String mobileNo, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
    }

    public UserDetails() {

    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }
}
