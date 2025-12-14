package com.example.coderelief.models;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 데일리 기록 모델 클래스
 * DB 스키마 daily_record 테이블과 매핑
 */
public class DailyRecord {
    private Long recordId;
    private Long patientId;
    private Long caregiverId;
    private String recordDate; // yyyy-MM-dd 형식의 문자열로 변경
    private String timeSlot; // BREAKFAST, LUNCH, DINNER
    private String meal; // GOOD, NORMAL, BAD
    private String healthCondition; // GOOD, NORMAL, BAD
    private boolean medicationTaken;
    private String notes;
    private String createdAt; // LocalDateTime을 String으로 받기 위해 변경

    // 기본 생성자
    public DailyRecord() {}

    // 전체 필드 생성자
    public DailyRecord(Long recordId, Long patientId, Long caregiverId, String recordDate,
                      String timeSlot, String meal, String healthCondition, 
                      boolean medicationTaken, String notes, String createdAt) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.caregiverId = caregiverId;
        this.recordDate = recordDate;
        this.timeSlot = timeSlot;
        this.meal = meal;
        this.healthCondition = healthCondition;
        this.medicationTaken = medicationTaken;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
    
    // Date 객체를 받아서 yyyy-MM-dd 형식의 문자열로 변환하는 메서드
    public void setRecordDate(Date recordDate) {
        if (recordDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            this.recordDate = dateFormat.format(recordDate);
        } else {
            this.recordDate = null;
        }
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getHealthCondition() {
        return healthCondition;
    }

    public void setHealthCondition(String healthCondition) {
        this.healthCondition = healthCondition;
    }

    public boolean isMedicationTaken() {
        return medicationTaken;
    }

    public void setMedicationTaken(boolean medicationTaken) {
        this.medicationTaken = medicationTaken;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DailyRecord{" +
                "recordId=" + recordId +
                ", patientId=" + patientId +
                ", timeSlot='" + timeSlot + '\'' +
                ", meal='" + meal + '\'' +
                ", healthCondition='" + healthCondition + '\'' +
                ", medicationTaken=" + medicationTaken +
                ", notes='" + notes + '\'' +
                '}';
    }
}

