package com.example.coderelief.models;

public class ChatRoom {
    private Long chatRoomId;
    private Long patientId;
    private String roomName;
    private String roomType;
    private Long guardianId;
    private Long caregiverId;
    private Long institutionId;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    // Constructors
    public ChatRoom() {}

    public ChatRoom(Long chatRoomId, Long patientId, String roomName, String roomType,
                   Long guardianId, Long caregiverId, Long institutionId, Boolean isActive,
                   String createdAt, String updatedAt) {
        this.chatRoomId = chatRoomId;
        this.patientId = patientId;
        this.roomName = roomName;
        this.roomType = roomType;
        this.guardianId = guardianId;
        this.caregiverId = caregiverId;
        this.institutionId = institutionId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Long getGuardianId() { return guardianId; }
    public void setGuardianId(Long guardianId) { this.guardianId = guardianId; }

    public Long getCaregiverId() { return caregiverId; }
    public void setCaregiverId(Long caregiverId) { this.caregiverId = caregiverId; }

    public Long getInstitutionId() { return institutionId; }
    public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isActive() {
        return isActive != null && isActive;
    }

    public String getDisplayName() {
        if (roomName != null && !roomName.isEmpty()) {
            return roomName;
        }
        return "채팅방 " + chatRoomId;
    }
}


