package com.ansimyoyang.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthAnalysisRequest {
    private Long patientId;
    private String patientName;
    private int age;
    private String gender;
    private String chronicDiseases; // 지병 정보
    private LocalDate analysisStartDate;
    private LocalDate analysisEndDate;
    private List<DailyRecordSummary> dailyRecords;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRecordSummary {
        private LocalDate recordDate;
        private String timeSlot;
        private String mealStatus;
        private String healthCondition;
        private boolean medicationTaken;
        private String notes;
    }
}
