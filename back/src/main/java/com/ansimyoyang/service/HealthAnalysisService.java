package com.ansimyoyang.service;

import com.ansimyoyang.domain.DailyRecord;
import com.ansimyoyang.domain.Patient;
import com.ansimyoyang.domain.dto.HealthAnalysisRequest;
import com.ansimyoyang.domain.dto.HealthAnalysisResponse;
import com.ansimyoyang.repository.DailyRecordRepository;
import com.ansimyoyang.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthAnalysisService {

    private final PatientRepository patientRepository;
    private final DailyRecordRepository dailyRecordRepository;
    private final OpenAIService openAIService;
    private final NotificationService notificationService;

    public HealthAnalysisResponse analyzePatientHealth(Long patientId, int days) {
        try {
            log.info("환자 건강상태 분석 시작: patientId={}, days={}", patientId, days);
            
            // 환자 정보 조회
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("환자를 찾을 수 없습니다: " + patientId));
            
            // 분석 기간 설정
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            // 해당 기간의 데일리기록 조회
            List<DailyRecord> dailyRecords = dailyRecordRepository
                    .findByPatient_PatientIdAndRecordDateBetweenOrderByRecordDateDesc(patientId, startDate, endDate, Pageable.unpaged())
                    .getContent();
            
            if (dailyRecords.isEmpty()) {
                return HealthAnalysisResponse.builder()
                        .success(false)
                        .message("분석할 데일리기록이 없습니다.")
                        .build();
            }
            
            // 요청 객체 생성
            HealthAnalysisRequest request = buildAnalysisRequest(patient, dailyRecords, startDate, endDate);
            
            // OpenAI 분석 수행
            HealthAnalysisResponse response = openAIService.analyzeHealthStatus(request);
            
            // 분석 결과가 주의가 필요한 경우 알림 생성
            if (response.isSuccess() && response.getAnalysisResult() != null && 
                response.getAnalysisResult().isNeedsAttention()) {
                createHealthAlertNotification(patient, response);
            }
            
            log.info("환자 건강상태 분석 완료: patientId={}, success={}", patientId, response.isSuccess());
            return response;
            
        } catch (Exception e) {
            log.error("환자 건강상태 분석 실패: patientId={}, error={}", patientId, e.getMessage(), e);
            
            return HealthAnalysisResponse.builder()
                    .success(false)
                    .message("건강상태 분석 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    private HealthAnalysisRequest buildAnalysisRequest(Patient patient, List<DailyRecord> dailyRecords, 
                                                     LocalDate startDate, LocalDate endDate) {
        // 나이 계산
        int age = patient.getBirthdate() != null ? 
                Period.between(patient.getBirthdate(), LocalDate.now()).getYears() : 0;
        
        // 데일리기록 요약 생성
        List<HealthAnalysisRequest.DailyRecordSummary> recordSummaries = dailyRecords.stream()
                .map(record -> HealthAnalysisRequest.DailyRecordSummary.builder()
                        .recordDate(record.getRecordDate())
                        .timeSlot(record.getTimeSlot().name())
                        .mealStatus(record.getMeal().name())
                        .healthCondition(record.getCondition() != null ? record.getCondition().name() : "NORMAL")
                        .medicationTaken(record.isMedicationTaken())
                        .notes(record.getNotes())
                        .build())
                .collect(Collectors.toList());
        
        // 지병 정보 (현재는 하드코딩, 실제로는 별도 테이블에서 조회)
        String chronicDiseases = getChronicDiseases(patient);
        
        return HealthAnalysisRequest.builder()
                .patientId(patient.getPatientId())
                .patientName(patient.getName())
                .age(age)
                .gender(patient.getGender() != null ? patient.getGender().name() : "UNKNOWN")
                .chronicDiseases(chronicDiseases)
                .analysisStartDate(startDate)
                .analysisEndDate(endDate)
                .dailyRecords(recordSummaries)
                .build();
    }

    private String getChronicDiseases(Patient patient) {
        // 실제로는 별도의 지병 테이블에서 조회해야 함
        // 현재는 나이와 성별에 따른 일반적인 지병 정보 제공
        int age = patient.getBirthdate() != null ? 
                Period.between(patient.getBirthdate(), LocalDate.now()).getYears() : 0;
        
        if (age >= 80) {
            return "고혈압, 당뇨, 관절염 가능성";
        } else if (age >= 70) {
            return "고혈압, 당뇨 가능성";
        } else {
            return "기본적인 노화 관련 증상";
        }
    }

    private void createHealthAlertNotification(Patient patient, HealthAnalysisResponse response) {
        try {
            String title = "건강상태 주의 알림 - " + patient.getName();
            String message = "AI 분석 결과 주의가 필요한 건강상태 변화가 감지되었습니다. 상세 내용을 확인해주세요.";
            
            // 보호자에게 알림
            if (patient.getGuardian() != null) {
                notificationService.createNotification(
                        patient.getGuardian().getGuardianId(),
                        com.ansimyoyang.domain.Notification.UserType.GUARDIAN,
                        title,
                        message,
                        com.ansimyoyang.domain.Notification.NotificationType.HEALTH_ALERT
                );
            }
            
            // 요양원 직원들에게 알림 (해당 기관의 모든 직원)
            // 실제로는 기관별 직원 조회 후 알림 발송
            
            log.info("건강상태 주의 알림 생성 완료: patientId={}", patient.getPatientId());
            
        } catch (Exception e) {
            log.error("건강상태 주의 알림 생성 실패: patientId={}, error={}", 
                    patient.getPatientId(), e.getMessage(), e);
        }
    }
}
