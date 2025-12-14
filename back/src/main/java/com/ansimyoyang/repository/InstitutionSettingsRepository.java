package com.ansimyoyang.repository;

import com.ansimyoyang.domain.InstitutionSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionSettingsRepository extends JpaRepository<InstitutionSettings, Long> {
    
    Optional<InstitutionSettings> findByInstitutionId(Long institutionId);
}