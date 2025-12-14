package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Activity;
import com.ansimyoyang.domain.dto.ActivityDto;
import com.ansimyoyang.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // front 연동을 위한 CORS 설정
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    // 활동 기록 저장 (CaregiverActivityRecordFragment의 btn_save)
    @PostMapping
    public java.util.Map<String, Object> save(@RequestBody ActivityDto dto) {
        try {
            log.info("활동 기록 저장 요청: patientId={}, caregiverId={}, type={}", 
                dto.getPatientId(), dto.getCaregiverId(), dto.getType());
            
            Activity savedActivity = activityService.save(dto);
            
            log.info("활동 기록 저장 성공: activityId={}", savedActivity.getActivityId());
            
            // 간단한 응답 메시지 반환
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "활동 기록이 성공적으로 저장되었습니다");
            response.put("activityId", savedActivity.getActivityId());
            
            return response;
        } catch (Exception e) {
            log.error("활동 기록 저장 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "활동 기록 저장 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    // 전체 활동 기록 조회 (Front에서 사용)
    @GetMapping
    public List<Activity> getAllActivities() {
        try {
            List<Activity> activities = activityService.getAllActivities();
            return activities;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("활동 목록 조회 중 오류 발생: " + e.getMessage());
        }
    }
    
    // 환자별 활동 기록 조회
    @GetMapping("/patient")
    public List<Activity> getActivitiesByPatient(
            @RequestParam(name = "patientId") Long patientId
    ) {
        return activityService.getActivitiesByPatient(patientId);
    }

    // 활동 타입별 조회 (급여, 활동프로그램 등)
    @GetMapping("/type")
    public List<Activity> getActivitiesByType(
            @RequestParam(name = "patientId") Long patientId,
            @RequestParam(name = "type") String type
    ) {
        return activityService.getActivitiesByType(patientId, type);
    }

    // 최근 활동 기록 조회 (GuardianPatientDetailFragment의 최근 사진 섹션용)
    @GetMapping("/recent")
    public List<Activity> getRecentActivities(
            @RequestParam(name = "patientId") Long patientId,
            @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        return activityService.getRecentActivities(patientId, limit);
    }
    
    // 간단한 테스트용 엔드포인트
    @GetMapping("/test")
    public String test() {
        return "Activity Controller is working!";
    }
}
