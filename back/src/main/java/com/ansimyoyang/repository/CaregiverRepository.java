package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {
    List<Caregiver> findByInstitution_InstitutionId(Long institutionId);
    Optional<Caregiver> findByAccountId(Long accountId);
}
