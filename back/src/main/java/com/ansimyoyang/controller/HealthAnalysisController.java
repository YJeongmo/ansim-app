package com.ansimyoyang.controller;

import com.ansimyoyang.domain.dto.HealthAnalysisResponse;
import com.ansimyoyang.service.HealthAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health-analysis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class HealthAnalysisController {

    private final HealthAnalysisService healthAnalysisService;

    /**
     * 환자의 건강상태 분석
     * @param patientId 환자 ID
     * @param days 분석할 일수 (3-7일 권장)
     * @return 건강상태 분석 결과
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Map<String, Object>> analyzePatientHealth(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "5") int days) {
        
        try {
            log.info("건강상태 분석 요청: patientId={}, days={}", patientId, days);
            
            // 일수 유효성 검사
            if (days < 1 || days > 30) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "분석 일수는 1일에서 30일 사이여야 합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            HealthAnalysisResponse result = healthAnalysisService.analyzePatientHealth(patientId, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("건강상태 분석 API 오류: patientId={}, error={}", patientId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "건강상태 분석 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 테스트용 건강상태 분석 (샘플 데이터 사용)
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testHealthAnalysis() {
        try {
            log.info("테스트 건강상태 분석 요청");
            
            // 테스트용으로 환자 ID 1번 사용
            HealthAnalysisResponse result = healthAnalysisService.analyzePatientHealth(1L, 5);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("테스트 건강상태 분석 오류: error={}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "테스트 분석 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
