package com.ansimyoyang.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequest {
    
    private Long requestId;
    private Long institutionId;
    private String institutionName;
    private String applicantName;
    private String applicantPhone;
    private String consultationPurpose;
    private String consultationContent;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 상담 신청 정보 요약 문자열 반환
    public String getConsultationSummary() {
        return String.format("[%s] %s - %s", 
            institutionName, 
            applicantName, 
            consultationPurpose);
    }
}



