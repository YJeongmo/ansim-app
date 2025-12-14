package com.ansimyoyang.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationApprovalDto {
    
    private Long appointmentId;
    private String approvalStatus; // APPROVED, REJECTED
    private String staffNotes; // 처리 사유
    private Long approvedBy; // 처리한 직원 ID
    
    // 시간 조정 (승인 시에만 사용)
    private LocalDateTime adjustedStartTime;
    private LocalDateTime adjustedEndTime;
    
    // 처리 사유 상세
    private String changeReason;
}