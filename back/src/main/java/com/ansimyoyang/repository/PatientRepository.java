package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    // 기관별 환자 목록
    @Query("SELECT p FROM Patient p WHERE p.institution.institutionId = :institutionId ORDER BY p.name")
    List<Patient> findByInstitutionInstitutionIdOrderByName(@Param("institutionId") Long institutionId);
    
    // 기관별 환자 목록 (간단한 버전)
    List<Patient> findByInstitution_InstitutionId(Long institutionId);
    
    // 보호자별 환자 정보
    Patient findByGuardian_GuardianId(Long guardianId);
    
    // 요양보호사가 담당하는 환자 목록
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.dailyRecords dr WHERE dr.caregiver.caregiverId = :caregiverId")
    List<Patient> findByCaregiverId(@Param("caregiverId") Long caregiverId);
}
