package com.ansimyoyang.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityCheckDto {
    
    private Long institutionId;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int requestedVisitors;
    
    // 응답 정보
    private boolean isAvailable;
    private String reasonIfNotAvailable;
    private List<String> conflictReasons;
    
    // 요양원 운영 정보
    private LocalTime institutionStartTime;
    private LocalTime institutionEndTime;
    private int maxConcurrentVisits;
    private int maxVisitorsPerReservation;
    private int currentReservations;
    private int currentVisitors;
    
    // 대안 제안
    private List<AlternativeTimeSlot> suggestedAlternatives;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternativeTimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        private int availableVisitors;
    }
}