package com.ansimyoyang.domain.dto;

import com.ansimyoyang.domain.DailyRecord;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRecordResponseDto {

    private Long recordId;          // <-- dailyRecordId 아님. recordId 로 통일
    private Long patientId;
    private Long caregiverId;

    private LocalDate recordDate;
    private String timeSlot;        // BREAKFAST / LUNCH / DINNER
    private String meal;            // GOOD / NORMAL / BAD
    private String condition;       // GOOD / NORMAL / BAD

    private boolean medicationTaken; // 복약 여부 (boolean)
    private String notes;

    private LocalDateTime createdAt;

    public static DailyRecordResponseDto from(DailyRecord e) {
        return DailyRecordResponseDto.builder()
                .recordId(e.getRecordId())
                .patientId(e.getPatient() != null ? e.getPatient().getPatientId() : null)
                .caregiverId(e.getCaregiver() != null ? e.getCaregiver().getCaregiverId() : null)
                .recordDate(e.getRecordDate())
                .timeSlot(e.getTimeSlot() != null ? e.getTimeSlot().name() : null)
                .meal(e.getMeal() != null ? e.getMeal().name() : null)
                .condition(e.getCondition() != null ? e.getCondition().name() : null)
                .medicationTaken(e.isMedicationTaken())
                .notes(e.getNotes())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
