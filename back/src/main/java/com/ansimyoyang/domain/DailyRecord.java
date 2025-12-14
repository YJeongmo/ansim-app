package com.ansimyoyang.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private Caregiver caregiver;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", nullable = false)
    private TimeSlot timeSlot;            // BREAKFAST / LUNCH / DINNER

    @Enumerated(EnumType.STRING)
    @Column(name = "meal", nullable = false)
    private Level meal;                   // GOOD / NORMAL / BAD

    @Enumerated(EnumType.STRING)
    @Column(name = "health_condition")   // ← 예약어 회피: condition 대신 health_condition 사용
    private Level condition;

    @Column(name = "medication_taken", nullable = false)
    private boolean medicationTaken;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ✅ 여기 두 enum이 꼭 있어야 DailyRecord.Level / DailyRecord.TimeSlot이 인식됩니다.
    public static enum TimeSlot { BREAKFAST, LUNCH, DINNER }
    public static enum Level { GOOD, NORMAL, BAD }
}
