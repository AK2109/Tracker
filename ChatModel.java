package com.example.friendtracker.Model;

public class ChatModel {
    String messageText, receiverId, senderId;
    long timeStamp;

    public ChatModel() {
    }

    public ChatModel(String messageText, String receiverId, String senderId, long timeStamp) {
        this.messageText = messageText;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
