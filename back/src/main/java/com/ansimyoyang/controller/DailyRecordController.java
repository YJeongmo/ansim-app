package com.ansimyoyang.controller;

import com.ansimyoyang.domain.dto.DailyRecordDto;
import com.ansimyoyang.domain.dto.DailyRecordResponseDto;
import com.ansimyoyang.service.DailyRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // front 연동을 위한 CORS 설정
public class DailyRecordController {

    private final DailyRecordService dailyRecordService;

    // 급여 기록 저장 (CaregiverMealRecordFragment의 btn_save)
    @PostMapping
    public DailyRecordResponseDto create(@RequestBody DailyRecordDto dto) {
        return dailyRecordService.create(dto);
    }

    // 환자별 급여 기록 조회
    @GetMapping("/patient/{patientId}")
    public List<DailyRecordResponseDto> getDailyRecordsByPatient(@PathVariable Long patientId) {
        return dailyRecordService.getDailyRecordsByPatient(patientId);
    }

    // 특정 날짜의 급여 기록 조회
    @GetMapping("/patient/{patientId}/date/{date}")
    public List<DailyRecordResponseDto> getDailyRecordsByDate(
            @PathVariable Long patientId,
            @PathVariable String date
    ) {
        return dailyRecordService.getDailyRecordsByDate(patientId, date);
    }
}
