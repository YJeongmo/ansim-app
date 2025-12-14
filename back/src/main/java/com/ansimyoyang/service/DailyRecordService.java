package com.ansimyoyang.service;

import com.ansimyoyang.domain.Caregiver;
import com.ansimyoyang.domain.DailyRecord;
import com.ansimyoyang.domain.Patient;
import com.ansimyoyang.domain.dto.DailyRecordDto;
import com.ansimyoyang.domain.dto.DailyRecordResponseDto;
import com.ansimyoyang.repository.CaregiverRepository;
import com.ansimyoyang.repository.DailyRecordRepository;
import com.ansimyoyang.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyRecordService {

    private final DailyRecordRepository dailyRecordRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    
 // ✅ 새로 추가: 하루치(최대 3건) 타임슬롯 오름차순 반환
    @Transactional(readOnly = true)
    public List<DailyRecordResponseDto> byDate(Long patientId, LocalDate date) {
        return dailyRecordRepository
                .findByPatient_PatientIdAndRecordDateOrderByTimeSlotAsc(patientId, date)
                .stream()
                .map(DailyRecordResponseDto::from)
                .toList();
    }

    public DailyRecordResponseDto create(DailyRecordDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + dto.getPatientId()));
        Caregiver caregiver = caregiverRepository.findById(dto.getCaregiverId())
                .orElseThrow(() -> new IllegalArgumentException("Caregiver not found: " + dto.getCaregiverId()));

        DailyRecord entity = DailyRecord.builder()
                .patient(patient)
                .caregiver(caregiver)
                .recordDate(dto.getRecordDate())
                .timeSlot(dto.getTimeSlot() != null ? dto.getTimeSlot() : DailyRecord.TimeSlot.LUNCH)
                .meal(dto.getMeal() != null ? dto.getMeal() : DailyRecord.Level.NORMAL)
                .condition(dto.getCondition() != null ? dto.getCondition() : DailyRecord.Level.NORMAL)
                .medicationTaken(Boolean.TRUE.equals(dto.getMedicationTaken()))
                .notes(dto.getNotes())
                .build();

        // 같은 날짜/타임슬롯이 이미 있으면 update처럼 동작 (선택)
        dailyRecordRepository.findByPatient_PatientIdAndRecordDateAndTimeSlot(
                dto.getPatientId(), dto.getRecordDate(), entity.getTimeSlot()
        ).ifPresent(existing -> entity.setRecordId(existing.getRecordId()));

        DailyRecord saved = dailyRecordRepository.save(entity);
        return DailyRecordResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<DailyRecordResponseDto> list(Long patientId, Pageable pageable) {
        return dailyRecordRepository
                .findByPatient_PatientIdOrderByRecordDateDesc(patientId, pageable)
                .map(DailyRecordResponseDto::from);
    }

    @Transactional(readOnly = true)
    public List<DailyRecordResponseDto> getDay(Long patientId, LocalDate date) {
        return dailyRecordRepository
                .findByPatient_PatientIdAndRecordDate(patientId, date)
                .stream()
                .map(DailyRecordResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<DailyRecordResponseDto> search(Long patientId,
                                               LocalDate from,
                                               LocalDate to,
                                               List<DailyRecord.TimeSlot> timeSlots,
                                               Pageable pageable) {
        if (timeSlots != null && !timeSlots.isEmpty()) {
            return dailyRecordRepository
                    .findByPatient_PatientIdAndRecordDateBetweenAndTimeSlotInOrderByRecordDateDesc(
                            patientId, from, to, timeSlots, pageable)
                    .map(DailyRecordResponseDto::from);
        }
        return dailyRecordRepository
                .findByPatient_PatientIdAndRecordDateBetweenOrderByRecordDateDesc(
                        patientId, from, to, pageable)
                .map(DailyRecordResponseDto::from);
    }

    // 편의 메서드: "BREAKFAST,LUNCH" 같은 문자열을 List<TimeSlot>로 변환하고 검색
    @Transactional(readOnly = true)
    public Page<DailyRecordResponseDto> search(Long patientId,
                                               LocalDate from,
                                               LocalDate to,
                                               String timeSlotsCsv,
                                               Pageable pageable) {
        List<DailyRecord.TimeSlot> slots = null;
        if (timeSlotsCsv != null && !timeSlotsCsv.isBlank()) {
            slots = Arrays.stream(timeSlotsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .map(DailyRecord.TimeSlot::valueOf)
                    .toList();
        }
        return search(patientId, from, to, slots, pageable);
    }

    // 환자별 급여 기록 조회 (PatientController에서 사용)
    @Transactional(readOnly = true)
    public List<DailyRecordResponseDto> getDailyRecordsByPatient(Long patientId) {
        return dailyRecordRepository
                .findByPatient_PatientIdOrderByRecordDateDesc(patientId, Pageable.unpaged())
                .map(DailyRecordResponseDto::from)
                .getContent();
    }

    // 특정 날짜의 급여 기록 조회 (PatientController에서 사용)
    @Transactional(readOnly = true)
    public List<DailyRecordResponseDto> getDailyRecordsByDate(Long patientId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return dailyRecordRepository
                .findByPatient_PatientIdAndRecordDate(patientId, date)
                .stream()
                .map(DailyRecordResponseDto::from)
                .toList();
    }
}
