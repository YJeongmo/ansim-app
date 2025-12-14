package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    // 기관별 전체 공지사항
    Page<Notice> findByInstitution_InstitutionIdAndIsPersonalFalseOrderByCreatedAtDesc(
            Long institutionId, Pageable pageable);
    
    // 기관별 개별 공지사항
    List<Notice> findByInstitution_InstitutionIdAndIsPersonalTrueAndPatient_PatientIdOrderByCreatedAtDesc(
            Long institutionId, Long patientId);
    
    // 환자별 개별 공지사항
    List<Notice> findByPatient_PatientIdOrderByCreatedAtDesc(Long patientId);
    
    // 요양보호사가 작성한 공지사항
    List<Notice> findByCaregiver_CaregiverIdOrderByCreatedAtDesc(Long caregiverId);
    
    // 제목으로 검색
    @Query("SELECT n FROM Notice n WHERE n.institution.institutionId = :institutionId " +
           "AND n.isPersonal = false AND n.title LIKE %:keyword% ORDER BY n.createdAt DESC")
    Page<Notice> searchByTitle(@Param("institutionId") Long institutionId, 
                               @Param("keyword") String keyword, Pageable pageable);
}

