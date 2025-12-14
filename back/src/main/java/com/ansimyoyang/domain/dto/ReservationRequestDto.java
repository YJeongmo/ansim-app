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
public class ReservationRequestDto {
    
    // 기본 예약 정보
    private Long patientId;
    private Long guardianId;
    private String appointmentType; // VISIT, OUTING, OVERNIGHT, CONSULTATION
    
    // 시간 정보
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // 예약 상세
    private String purpose;
    private String reason;
    private String guardianNotes;
    private String visitorRelationship; // 방문자와 어르신과의 관계
    
    // 동반자 정보
    private int visitorCount;
    private List<CompanionDto> companions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanionDto {
        private String companionName;
        private String companionRelationship;
        private Integer companionAge;
    }
}