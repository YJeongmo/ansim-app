package com.example.coderelief.models;

import java.time.LocalDateTime;

public class ChatMessage {
    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String senderType; // "GUARDIAN" or "CAREGIVER"
    private String messageText;
    private String messageType; // "TEXT", "IMAGE", "FILE"
    private Boolean isRead;
    private String sentAt;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(Long messageId, Long chatRoomId, Long senderId, String senderType, 
                      String messageText, String messageType, Boolean isRead, String sentAt) {
        this.messageId = messageId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.senderType = senderType;
        this.messageText = messageText;
        this.messageType = messageType;
        this.isRead = isRead;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    // Helper methods
    public boolean isFromGuardian() {
        return "GUARDIAN".equals(senderType);
    }

    public boolean isFromCaregiver() {
        return "CAREGIVER".equals(senderType);
    }

    public boolean isTextMessage() {
        return "TEXT".equals(messageType);
    }

    // Format sent time for display
    public String getFormattedTime() {
        if (sentAt == null || sentAt.isEmpty()) {
            return "";
        }
        
        try {
            // Parse ISO datetime string and format for display
            // Example: "2025-01-08T19:30:00" -> "19:30"
            if (sentAt.contains("T")) {
                String timePart = sentAt.split("T")[1];
                if (timePart.length() >= 5) {
                    return timePart.substring(0, 5); // HH:mm
                }
            }
            return sentAt;
        } catch (Exception e) {
            return sentAt;
        }
    }
}


