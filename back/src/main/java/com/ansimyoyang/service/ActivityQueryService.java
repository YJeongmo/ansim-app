package com.ansimyoyang.service;

import com.ansimyoyang.domain.Activity;
import com.ansimyoyang.domain.dto.ActivityQueryDto;
import com.ansimyoyang.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityQueryService {

    private final ActivityRepository activityRepository;

    public Page<ActivityQueryDto> list(Long patientId,
                                       LocalDateTime from,
                                       LocalDateTime to,
                                       String type,
                                       Pageable pageable) {

        Page<Activity> page;
        boolean hasRange = (from != null && to != null);
        boolean hasType  = (type != null && !type.isBlank());

        if (hasRange && hasType) {
            page = activityRepository
                    .findByPatient_PatientIdAndActivityTimeBetweenAndTypeContainingIgnoreCaseOrderByActivityTimeDesc(
                            patientId, from, to, type, pageable);
        } else if (hasRange) {
            page = activityRepository
                    .findByPatient_PatientIdAndActivityTimeBetweenOrderByActivityTimeDesc(
                            patientId, from, to, pageable);
        } else if (hasType) {
            page = activityRepository
                    .findByPatient_PatientIdAndTypeContainingIgnoreCaseOrderByActivityTimeDesc(
                            patientId, type, pageable);
        } else {
            page = activityRepository
                    .findByPatient_PatientIdOrderByActivityTimeDesc(patientId, pageable);
        }

        return page.map(ActivityQueryDto::from);
    }
}
