package model;

import java.time.LocalDateTime;

public class Notification {

    private int notificationId;
    private int userId;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;

    public Notification() {
    }

    public Notification(int notificationId, int userId, String message,
                        LocalDateTime createdAt, boolean read) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public Notification(int userId, String message, LocalDateTime createdAt, boolean read) {
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}