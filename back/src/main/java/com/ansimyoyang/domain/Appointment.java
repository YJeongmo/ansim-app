package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "activities", "dailyRecords"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "patient"})
    private Guardian guardian;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "purpose")
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type")
    @Builder.Default
    private AppointmentType appointmentType = AppointmentType.VISIT;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "visitor_count")
    @Builder.Default
    private Integer visitorCount = 1;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "guardian_notes", columnDefinition = "TEXT")
    private String guardianNotes;

    @Column(name = "staff_notes", columnDefinition = "TEXT")
    private String staffNotes;

    @Column(name = "visitor_relationship")
    private String visitorRelationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "activities", "institution"})
    private Caregiver approvedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AppointmentType {
        VISIT, OUTING, OVERNIGHT, CONSULTATION
    }

    public enum AppointmentStatus {
        REQUEST, APPROVED, REJECTED, PENDING, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getAppointmentType() {
        return appointmentType != null ? appointmentType.name() : null;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType != null ? 
            AppointmentType.valueOf(appointmentType) : AppointmentType.VISIT;
    }

    public String getStatus() {
        return status != null ? status.name() : null;
    }

    public void setStatus(String status) {
        this.status = status != null ? 
            AppointmentStatus.valueOf(status) : AppointmentStatus.PENDING;
    }
}