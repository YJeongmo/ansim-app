package com.ansimyoyang.domain.dto;

import com.ansimyoyang.domain.Activity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityQueryDto {
    private Long activityId;
    private String type;
    private String description;
    private String photoUrl;
    private LocalDateTime activityTime;

    private Long patientId;
    private String patientName;

    private Long caregiverId;
    private String caregiverName;

    public static ActivityQueryDto from(Activity a) {
        return ActivityQueryDto.builder()
                .activityId(a.getActivityId())
                .type(a.getType())
                .description(a.getDescription())
                .photoUrl(a.getPhotoUrl())
                .activityTime(a.getActivityTime())
                .patientId(a.getPatient() != null ? a.getPatient().getPatientId() : null)
                .patientName(a.getPatient() != null ? a.getPatient().getName() : null)
                .caregiverId(a.getCaregiver() != null ? a.getCaregiver().getCaregiverId() : null)
                .caregiverName(a.getCaregiver() != null ? a.getCaregiver().getName() : null)
                .build();
    }
}
