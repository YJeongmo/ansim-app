package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Activity;
import com.ansimyoyang.domain.DailyRecord;
import com.ansimyoyang.domain.Institution;
import com.ansimyoyang.domain.Patient;
import com.ansimyoyang.repository.ActivityRepository;
import com.ansimyoyang.repository.DailyRecordRepository;
import com.ansimyoyang.repository.InstitutionRepository;
import com.ansimyoyang.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {

    private final InstitutionRepository institutionRepository;
    private final PatientRepository patientRepository;
    private final ActivityRepository activityRepository;
    private final DailyRecordRepository dailyRecordRepository;

    // DB 연결 상태 및 데이터 현황 확인
    @GetMapping("/status")
    public Map<String, Object> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            long institutionCount = institutionRepository.count();
            long patientCount = patientRepository.count();
            long activityCount = activityRepository.count();
            long dailyRecordCount = dailyRecordRepository.count();
            
            status.put("status", "SUCCESS");
            status.put("message", "데이터베이스 연결 성공");
            status.put("data", Map.of(
                "institutions", institutionCount,
                "patients", patientCount,
                "activities", activityCount,
                "dailyRecords", dailyRecordCount
            ));
            
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("message", "데이터베이스 연결 실패: " + e.getMessage());
        }
        
        return status;
    }

    // 모든 데이터 조회 (테스트용)
    @GetMapping("/all-data")
    public Map<String, Object> getAllData() {
        Map<String, Object> allData = new HashMap<>();
        
        List<Institution> institutions = institutionRepository.findAll();
        List<Patient> patients = patientRepository.findAll();
        List<Activity> activities = activityRepository.findAll();
        List<DailyRecord> dailyRecords = dailyRecordRepository.findAll();
        
        allData.put("institutions", institutions);
        allData.put("patients", patients);
        allData.put("activities", activities);
        allData.put("dailyRecords", dailyRecords);
        
        return allData;
    }

    // 특정 환자의 모든 데이터 조회
    @GetMapping("/patient/{patientId}/all")
    public Map<String, Object> getPatientAllData(@PathVariable Long patientId) {
        Map<String, Object> patientData = new HashMap<>();
        
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient != null) {
            List<Activity> activities = activityRepository.findByPatient_PatientIdOrderByActivityTimeDesc(patientId, null).getContent();
            List<DailyRecord> dailyRecords = dailyRecordRepository.findByPatient_PatientIdOrderByRecordDateDesc(patientId, null).getContent();
            
            patientData.put("patient", patient);
            patientData.put("activities", activities);
            patientData.put("dailyRecords", dailyRecords);
        }
        
        return patientData;
    }
}
