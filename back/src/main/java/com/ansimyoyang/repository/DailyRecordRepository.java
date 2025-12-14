package com.ansimyoyang.repository;

import com.ansimyoyang.domain.DailyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {

    Page<DailyRecord> findByPatient_PatientIdOrderByRecordDateDesc(Long patientId, Pageable pageable);

    Page<DailyRecord> findByPatient_PatientIdAndRecordDateBetweenOrderByRecordDateDesc(
            Long patientId, LocalDate from, LocalDate to, Pageable pageable);

    Page<DailyRecord> findByPatient_PatientIdAndRecordDateBetweenAndTimeSlotInOrderByRecordDateDesc(
            Long patientId, LocalDate from, LocalDate to, List<DailyRecord.TimeSlot> timeSlots, Pageable pageable);

    List<DailyRecord> findByPatient_PatientIdAndRecordDate(Long patientId, LocalDate recordDate);

    Optional<DailyRecord> findByPatient_PatientIdAndRecordDateAndTimeSlot(
            Long patientId, LocalDate recordDate, DailyRecord.TimeSlot timeSlot);

    // ✅ 새로 추가: 같은 날짜의 3건을 time_slot 선언 순서(아침→점심→저녁)로 정렬
    List<DailyRecord> findByPatient_PatientIdAndRecordDateOrderByTimeSlotAsc(
            Long patientId, LocalDate recordDate);
}
