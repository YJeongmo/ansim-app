package com.example.coderelief.models;

import java.util.Date;

/**
 * 환자 정보 모델 클래스
 * DB 스키마 patient 테이블과 매핑
 */
public class Patient {
    private Long patientId;
    private String name;
    private String birthdate;
    private Object institution;
    private Object guardian;
    private Integer age;
    private String roomNumber;
    private String careLevel;
    private String admissionDate;

    // 기본 생성자
    public Patient() {}

    // 간편 생성자 (UI 표시용)
    public Patient(Long patientId, String name, Integer age, String roomNumber, String careLevel) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.roomNumber = roomNumber;
        this.careLevel = careLevel;
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Object getInstitution() {
        return institution;
    }

    public void setInstitution(Object institution) {
        this.institution = institution;
    }

    public Object getGuardian() {
        return guardian;
    }

    public void setGuardian(Object guardian) {
        this.guardian = guardian;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(String careLevel) {
        this.careLevel = careLevel;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", roomNumber='" + roomNumber + '\'' +
                ", careLevel='" + careLevel + '\'' +
                '}';
    }
}