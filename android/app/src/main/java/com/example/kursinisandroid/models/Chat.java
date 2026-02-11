package com.example.kursinisandroid.models;

import com.google.gson.annotations.SerializedName;

public class Chat {
    @SerializedName("id")
    private int id;

    @SerializedName("orderId")
    private int orderId;

    @SerializedName("senderId")
    private int senderId;

    @SerializedName("senderName")
    private String senderName;

    @SerializedName("message")
    private String message;

    @SerializedName("timestamp")
    private String timestamp;

    public Chat() {
    }

    public Chat(int id, int orderId, int senderId, String senderName, String message, String timestamp) {
        this.id = id;
        this.orderId = orderId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
