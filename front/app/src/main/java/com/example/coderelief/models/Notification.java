package com.example.coderelief.models;

import java.time.LocalDateTime;

/**
 * 알림 모델 클래스
 * 서버의 Notification 엔티티와 매칭
 */
public class Notification {
    
    private Long notificationId;
    private Long userId;
    private String userType; // GUARDIAN, CAREGIVER
    private String notificationType; // CHAT, MEAL, ACTIVITY, NOTICE, APPOINTMENT, CONSULTATION
    private String title;
    private String message;
    private Long relatedId;
    private boolean isSent;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    // Constructors
    public Notification() {}

    public Notification(Long notificationId, Long userId, String userType, String notificationType, 
                       String title, String message, Long relatedId, boolean isSent, boolean isRead,
                       LocalDateTime createdAt, LocalDateTime sentAt, LocalDateTime readAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.userType = userType;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.relatedId = relatedId;
        this.isSent = isSent;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.readAt = readAt;
    }

    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    // Helper methods
    public String getNotificationTypeKorean() {
        switch (notificationType) {
            case "CHAT": return "채팅";
            case "MEAL": return "급여";
            case "ACTIVITY": return "활동";
            case "NOTICE": return "공지";
            case "APPOINTMENT": return "예약";
            case "CONSULTATION": return "상담";
            default: return notificationType;
        }
    }

    public String getUserTypeKorean() {
        switch (userType) {
            case "GUARDIAN": return "보호자";
            case "CAREGIVER": return "요양보호사";
            default: return userType;
        }
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", userType='" + userType + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", relatedId=" + relatedId +
                ", isSent=" + isSent +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                '}';
    }
}