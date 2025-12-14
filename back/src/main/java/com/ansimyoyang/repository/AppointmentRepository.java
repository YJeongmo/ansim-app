package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByGuardian_GuardianIdOrderByStartTimeDesc(Long guardianId);
    
    List<Appointment> findByPatient_PatientIdOrderByStartTimeDesc(Long patientId);
    
    List<Appointment> findByStatusOrderByScheduledAtAsc(Appointment.AppointmentStatus status);
    
    List<Appointment> findAllByOrderByScheduledAtDesc();
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "(a.startTime <= :endTime AND a.endTime >= :startTime)")
    List<Appointment> findConflictingAppointments(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.status = :status AND " +
           "a.startTime >= :startDate AND a.startTime <= :endDate " +
           "ORDER BY a.startTime ASC")
    List<Appointment> findByStatusAndDateRange(
        @Param("status") Appointment.AppointmentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.startTime >= :startDate AND a.startTime <= :endDate " +
           "ORDER BY a.startTime ASC")
    List<Appointment> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // 기관별 예약 조회
    List<Appointment> findByPatient_Institution_InstitutionIdOrderByScheduledAtDesc(Long institutionId);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.patient.institution.institutionId = :institutionId AND " +
           "a.status = :status " +
           "ORDER BY a.scheduledAt ASC")
    List<Appointment> findByInstitutionAndStatus(
        @Param("institutionId") Long institutionId,
        @Param("status") Appointment.AppointmentStatus status
    );
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.patient.institution.institutionId = :institutionId AND " +
           "a.startTime >= :startDate AND a.startTime <= :endDate " +
           "ORDER BY a.startTime ASC")
    List<Appointment> findByInstitutionAndDateRange(
        @Param("institutionId") Long institutionId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.patient.institution.institutionId = :institutionId AND " +
           "a.status = :status AND " +
           "a.startTime >= :startDate AND a.startTime <= :endDate " +
           "ORDER BY a.startTime ASC")
    List<Appointment> findByInstitutionAndStatusAndDateRange(
        @Param("institutionId") Long institutionId,
        @Param("status") Appointment.AppointmentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}