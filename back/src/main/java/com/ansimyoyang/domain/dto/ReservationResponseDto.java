package com.ansimyoyang.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {
    
    // 예약 기본 정보
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private Long guardianId;
    private String guardianName;
    
    // 예약 유형 및 상태
    private String appointmentType;
    private String status;
    
    // 시간 정보
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime scheduledAt;
    
    // 예약 상세
    private String purpose;
    private String reason;
    private String guardianNotes;
    private String staffNotes;
    private String visitorRelationship; // 방문자와 어르신과의 관계
    
    // 동반자 정보
    private int visitorCount;
    private List<CompanionResponseDto> companions;
    
    // 처리 정보
    private Long approvedBy;
    private String approvedByName;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanionResponseDto {
        private Long companionId;
        private String companionName;
        private String companionRelationship;
        private Integer companionAge;
    }
}