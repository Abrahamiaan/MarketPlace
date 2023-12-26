package com.example.marketplace.Model;

public class NotificationModel {

    private String message;

    public NotificationModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}