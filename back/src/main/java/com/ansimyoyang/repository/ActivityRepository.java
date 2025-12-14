package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findByPatient_PatientIdOrderByActivityTimeDesc(Long patientId, Pageable pageable);

    Page<Activity> findByPatient_PatientIdAndActivityTimeBetweenOrderByActivityTimeDesc(
            Long patientId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Activity> findByPatient_PatientIdAndTypeContainingIgnoreCaseOrderByActivityTimeDesc(
            Long patientId, String type, Pageable pageable);

    Page<Activity> findByPatient_PatientIdAndActivityTimeBetweenAndTypeContainingIgnoreCaseOrderByActivityTimeDesc(
            Long patientId, LocalDateTime from, LocalDateTime to, String type, Pageable pageable);
}
