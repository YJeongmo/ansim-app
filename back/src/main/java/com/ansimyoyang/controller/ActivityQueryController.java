package com.ansimyoyang.controller;

import com.ansimyoyang.domain.dto.ActivityQueryDto;
import com.ansimyoyang.service.ActivityQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activity-query")
@RequiredArgsConstructor
public class ActivityQueryController {

    private final ActivityQueryService activityQueryService;

    @GetMapping
    public Page<ActivityQueryDto> list(
            @RequestParam(name = "patientId") Long patientId,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(name = "type", required = false) String type,
            Pageable pageable
    ) {
        return activityQueryService.list(patientId, from, to, type, pageable);
    }
}
