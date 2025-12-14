package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "patient")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "patients", "caregivers"})
    private Institution institution;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "patient"})
    private Guardian guardian;

    // 역방향 컬렉션은 JSON 순환을 막기 위해 무시
    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<Activity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<DailyRecord> dailyRecords = new ArrayList<>();

    // 프론트엔드 호환성을 위한 계산된 필드들
    @JsonProperty("age")
    @Transient
    public Integer getAge() {
        if (birthdate != null) {
            return Period.between(birthdate, LocalDate.now()).getYears();
        }
        return null;
    }

    @JsonProperty("roomNumber")
    @Transient
    public String getRoomNumber() {
        // 임시로 환자 ID를 기반으로 방호실 생성 (실제로는 별도 테이블이나 필드 필요)
        if (patientId != null) {
            return String.valueOf(100 + patientId);
        }
        return null;
    }

    @JsonProperty("careLevel")
    @Transient
    public String getCareLevel() {
        // 임시로 나이를 기반으로 요양등급 생성 (실제로는 별도 테이블이나 필드 필요)
        if (birthdate != null) {
            int age = Period.between(birthdate, LocalDate.now()).getYears();
            if (age >= 85) return "1";
            else if (age >= 80) return "2";
            else if (age >= 75) return "3";
            else if (age >= 70) return "4";
            else return "5";
        }
        return null;
    }

    @JsonProperty("admissionDate")
    @Transient
    public LocalDate getAdmissionDate() {
        // 임시로 생성일을 입소일로 사용 (실제로는 별도 필드 필요)
        return LocalDate.now().minusDays(30); // 30일 전 입소로 가정
    }

    public enum Gender {
        M, F
    }
}
