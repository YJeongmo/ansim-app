package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    
    // 요양원명과 전화번호로 기관 조회
    @Query("SELECT i FROM Institution i WHERE i.institutionName = :institutionName AND i.phoneNumber = :phoneNumber")
    Optional<Institution> findByInstitutionNameAndPhoneNumber(
        @Param("institutionName") String institutionName, 
        @Param("phoneNumber") String phoneNumber
    );
    
    // 요양원명으로 기관 조회
    Optional<Institution> findByInstitutionName(String institutionName);
    
    // 전화번호로 기관 조회
    Optional<Institution> findByPhoneNumber(String phoneNumber);
}