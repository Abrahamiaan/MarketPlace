package com.example.marketplace.Model;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationModel {
    private String notificationId;
    private String message;
    private String title;
    private String ownerId;
    private boolean isRead;
    private String timestamp;
    private String type;

    public NotificationModel() {
        this.timestamp = getCurrentTimestamp();
        this.isRead = false;
    }
    public NotificationModel(String message, String title, String ownerId, String type) {
        this(message, title);
        this.ownerId = ownerId;
        this.type = type;
        this.timestamp = getCurrentTimestamp();
    }

    private NotificationModel(String message, String title) {
        this.title = title;
        this.message = message;
        this.isRead = false;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentTimestamp() {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(currentDate);
    }
}