package com.example.coderelief.models;

import java.util.Date;

/**
 * 활동 기록 모델 클래스
 * DB 스키마 activity 테이블과 매핑
 */
public class Activity {
    private Long activityId;
    private Long patientId;
    private Long caregiverId;
    private String type;
    private String description;
    private String photoUrl;
    private String notes;
    private Date activityTime;
    private Date createdAt;

    // 기본 생성자
    public Activity() {}

    // 전체 필드 생성자
    public Activity(Long activityId, Long patientId, Long caregiverId, String type, 
                   String description, String photoUrl, String notes, Date activityTime, Date createdAt) {
        this.activityId = activityId;
        this.patientId = patientId;
        this.caregiverId = caregiverId;
        this.type = type;
        this.description = description;
        this.photoUrl = photoUrl;
        this.notes = notes;
        this.activityTime = activityTime;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(Long caregiverId) {
        this.caregiverId = caregiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date activityTime) {
        this.activityTime = activityTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", patientId=" + patientId +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", activityTime=" + activityTime +
                '}';
    }
}

