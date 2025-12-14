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
public class HealthAnalysisResponse {
    private boolean success;
    private String message;
    private HealthAnalysisResult analysisResult;
    private LocalDateTime analyzedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthAnalysisResult {
        private String overallAssessment; // 전체적인 건강상태 평가
        private List<String> concerns; // 우려사항 목록
        private List<String> recommendations; // 권장사항 목록
        private String detailedAnalysis; // 상세 분석 내용
        private boolean needsAttention; // 주의 필요 여부
    }
}
