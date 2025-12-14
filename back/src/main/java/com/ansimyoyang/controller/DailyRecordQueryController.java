package com.ansimyoyang.controller;

import com.ansimyoyang.domain.DailyRecord;
import com.ansimyoyang.service.DailyRecordService;
import com.ansimyoyang.domain.dto.DailyRecordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-records")
@RequiredArgsConstructor
public class DailyRecordQueryController {

    private final DailyRecordService dailyRecordService;

    // 2) 목록 페이징
    @GetMapping
    public Page<DailyRecordResponseDto> list(
            @RequestParam("patientId") Long patientId,
            Pageable pageable
    ) {
        return dailyRecordService.list(patientId, pageable);
    }

    // 3) 특정 날짜 3건
    @GetMapping("/by-date")
    public List<DailyRecordResponseDto> byDate(
            @RequestParam("patientId") Long patientId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return dailyRecordService.byDate(patientId, date);
    }

    // 4) 기간 + 슬롯 검색
    @GetMapping("/search")
    public Page<DailyRecordResponseDto> search(
            @RequestParam("patientId") Long patientId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "timeSlots", required = false)
            List<DailyRecord.TimeSlot> timeSlots,
            Pageable pageable
    ) {
        return dailyRecordService.search(patientId, from, to, timeSlots, pageable);
    }
}
