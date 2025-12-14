package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Patient;
import com.ansimyoyang.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // front 연동을 위한 CORS 설정
@Slf4j
public class PatientController {

    private final PatientRepository patientRepository;

    // 전체 환자 목록 조회
    @GetMapping("")
    public List<Patient> getAllPatients() {
        log.info("전체 환자 목록 조회 요청");
        List<Patient> patients = patientRepository.findAll();
        log.info("조회된 환자 수: {}", patients.size());
        return patients;
    }

    // 기관별 환자 목록 조회 (요양보호사용)
    @GetMapping("/institution/{institutionId}")
    public List<Patient> getPatientsByInstitution(@PathVariable Long institutionId) {
        log.info("기관별 환자 목록 조회 요청: institutionId = {}", institutionId);
        
        try {
            // 모든 환자를 가져와서 필터링 (임시 해결책)
            List<Patient> allPatients = patientRepository.findAll();
            List<Patient> filteredPatients = allPatients.stream()
                .filter(patient -> patient.getInstitution() != null && 
                                 patient.getInstitution().getInstitutionId().equals(institutionId))
                .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                .toList();
            
            log.info("조회된 환자 수: {}", filteredPatients.size());
            return filteredPatients;
        } catch (Exception e) {
            log.error("환자 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("환자 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 보호자별 환자 정보 조회
    @GetMapping("/guardian/{guardianId}")
    public Patient getPatientByGuardian(@PathVariable Long guardianId) {
        log.info("보호자별 환자 정보 조회 요청: guardianId = {}", guardianId);
        Patient patient = patientRepository.findByGuardian_GuardianId(guardianId);
        if (patient != null) {
            log.info("조회된 환자: ID={}, 이름={}", patient.getPatientId(), patient.getName());
        } else {
            log.warn("보호자 ID {}에 해당하는 환자를 찾을 수 없음", guardianId);
        }
        return patient;
    }

    // 요양보호사가 담당하는 환자 목록
    @GetMapping("/caregiver/{caregiverId}")
    public List<Patient> getPatientsByCaregiver(@PathVariable Long caregiverId) {
        log.info("요양보호사별 환자 목록 조회 요청: caregiverId = {}", caregiverId);
        List<Patient> patients = patientRepository.findByCaregiverId(caregiverId);
        log.info("조회된 환자 수: {}", patients.size());
        return patients;
    }

    // 환자 상세 정보 조회
    @GetMapping("/{patientId}")
    public Patient getPatientById(@PathVariable Long patientId) {
        log.info("환자 상세 정보 조회 요청: patientId = {}", patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("환자를 찾을 수 없습니다: " + patientId));
        log.info("조회된 환자: ID={}, 이름={}", patient.getPatientId(), patient.getName());
        return patient;
    }
}
