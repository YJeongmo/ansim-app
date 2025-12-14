package com.ansimyoyang.service;

import com.ansimyoyang.domain.DailyRecord;
import com.ansimyoyang.domain.dto.DailyRecordResponseDto;
import com.ansimyoyang.repository.DailyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyRecordQueryService {

    private final DailyRecordRepository dailyRecordRepository;

    /**
     * 특정 환자의 특정 날짜 DailyRecord 3건(아침/점심/저녁)을 timeSlot 순으로 반환
     */
    public List<DailyRecordResponseDto> listOfDay(Long patientId, LocalDate date) {
        List<DailyRecord> records =
                dailyRecordRepository.findByPatient_PatientIdAndRecordDateOrderByTimeSlotAsc(patientId, date);

        return records.stream()
                .map(DailyRecordResponseDto::from)
                .toList();
    }
}
