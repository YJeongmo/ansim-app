package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    
    Optional<Guardian> findByPatient_PatientId(Long patientId);
    
    Optional<Guardian> findByPhone(String phone);
    
    Optional<Guardian> findByAccountId(Long accountId);
}

