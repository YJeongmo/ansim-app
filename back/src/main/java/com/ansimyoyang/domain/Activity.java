package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "activity")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "activities", "dailyRecords"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "activities"})
    private Caregiver caregiver;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "activity_time", nullable = false)
    private LocalDateTime activityTime;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 프론트엔드 호환성을 위한 JSON 직렬화 메서드들
    @JsonProperty("patientId")
    public Long getPatientId() {
        return patient != null ? patient.getPatientId() : null;
    }

    @JsonProperty("caregiverId")
    public Long getCaregiverId() {
        return caregiver != null ? caregiver.getCaregiverId() : null;
    }

    // Date 타입으로 변환하여 프론트엔드와 호환
    @JsonProperty("activityTime")
    public Date getActivityTimeAsDate() {
        if (activityTime != null) {
            return Date.from(activityTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    @JsonProperty("createdAt")
    public Date getCreatedAtAsDate() {
        if (createdAt != null) {
            return Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }
}
