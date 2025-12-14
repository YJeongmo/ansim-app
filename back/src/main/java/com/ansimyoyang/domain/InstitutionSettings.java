package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "institution_settings")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InstitutionSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    @Column(name = "institution_id", nullable = false)
    private Long institutionId;

    @Column(name = "visit_start_time")
    @Builder.Default
    private LocalTime visitStartTime = LocalTime.of(9, 0);

    @Column(name = "visit_end_time")
    @Builder.Default
    private LocalTime visitEndTime = LocalTime.of(20, 0);

    @Column(name = "max_concurrent_visits")
    @Builder.Default
    private Integer maxConcurrentVisits = 5;

    @Column(name = "max_visitors_per_reservation")
    @Builder.Default
    private Integer maxVisitorsPerReservation = 3;

    @Column(name = "advance_booking_days")
    @Builder.Default
    private Integer advanceBookingDays = 14;

    @Column(name = "cancellation_deadline_hours")
    @Builder.Default
    private Integer cancellationDeadlineHours = 24;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}