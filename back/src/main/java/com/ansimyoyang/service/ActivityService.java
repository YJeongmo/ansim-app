package com.ansimyoyang.service;

import com.ansimyoyang.domain.Activity;
import com.ansimyoyang.domain.Caregiver;
import com.ansimyoyang.domain.Patient;
import com.ansimyoyang.domain.dto.ActivityDto;
import com.ansimyoyang.repository.ActivityRepository;
import com.ansimyoyang.repository.CaregiverRepository;
import com.ansimyoyang.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;   // ★ 이 import 꼭 필요
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final NotificationService notificationService;

    public Activity create(ActivityDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("환자 없음: " + dto.getPatientId()));
        Caregiver caregiver = caregiverRepository.findById(dto.getCaregiverId())
                .orElseThrow(() -> new IllegalArgumentException("요양보호사 없음: " + dto.getCaregiverId()));

        Activity activity = Activity.builder()
                .patient(patient)
                .caregiver(caregiver)
                .type(dto.getType())
                .description(dto.getDescription())
                .photoUrl(dto.getPhotoUrl())
                .activityTime(dto.getActivityTimeAsLocalDateTime()) // String을 LocalDateTime으로 변환
                .createdAt(LocalDateTime.now())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        
        // 보호자에게 알림 생성 (환자에게 보호자가 연결되어 있는 경우)
        try {
            if (patient.getGuardian() != null) {
                Long guardianId = patient.getGuardian().getGuardianId();
                String patientName = patient.getName();
                String activityType = dto.getType();
                
                if ("Meal".equalsIgnoreCase(activityType)) {
                    // 급여 알림
                    String mealType = "식사";
                    if (dto.getDescription() != null) {
                        if (dto.getDescription().contains("아침")) mealType = "아침식사";
                        else if (dto.getDescription().contains("점심")) mealType = "점심식사";
                        else if (dto.getDescription().contains("저녁")) mealType = "저녁식사";
                        else if (dto.getDescription().contains("간식")) mealType = "간식";
                    }
                    notificationService.createMealNotification(guardianId, patientName, mealType);
                } else {
                    // 일반 활동 알림
                    notificationService.createActivityNotification(guardianId, patientName, activityType, savedActivity.getActivityId());
                }
            }
        } catch (Exception e) {
            // 알림 생성 실패해도 활동 기록 저장은 성공으로 처리
            System.out.println("활동 알림 생성 실패: " + e.getMessage());
        }

        return savedActivity;
    }

    // ★ 컨트롤러 호환용 별칭
    public Activity save(ActivityDto dto) { return create(dto); }
    
    // 전체 활동 기록 조회
    @Transactional(readOnly = true)
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Activity> getByPatient(Long patientId) {
        // ★ Pageable.unpaged()로 호출하고 Page의 content만 꺼냄
        return activityRepository
                .findByPatient_PatientIdOrderByActivityTimeDesc(patientId, Pageable.unpaged())
                .getContent();
    }
    
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByPatient(Long patientId) {
        return activityRepository
                .findByPatient_PatientIdOrderByActivityTimeDesc(patientId, Pageable.unpaged())
                .getContent();
    }

    // 활동 타입별 조회 (급여, 활동프로그램 등)
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByType(Long patientId, String type) {
        return activityRepository
                .findByPatient_PatientIdAndTypeContainingIgnoreCaseOrderByActivityTimeDesc(patientId, type, Pageable.unpaged())
                .getContent();
    }

    // 최근 활동 기록 조회 (GuardianPatientDetailFragment의 최근 사진 섹션용)
    @Transactional(readOnly = true)
    public List<Activity> getRecentActivities(Long patientId, int limit) {
        return activityRepository
                .findByPatient_PatientIdOrderByActivityTimeDesc(patientId, Pageable.ofSize(limit))
                .getContent();
    }
}
