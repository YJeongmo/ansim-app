package com.ansimyoyang.domain.dto;

import com.ansimyoyang.domain.Notice;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeResponseDto {
    private Long noticeId;
    private String title;
    private String content;
    private String institutionName;
    private String caregiverName;
    private String patientName;
    private boolean isPersonal;
    private String priority;
    private String photoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Notice 엔티티를 NoticeResponseDto로 변환하는 정적 메서드
    public static NoticeResponseDto from(Notice notice) {
        NoticeResponseDto dto = new NoticeResponseDto();
        dto.setNoticeId(notice.getNoticeId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setInstitutionName(notice.getInstitution() != null ? notice.getInstitution().getInstitutionName() : null);
        dto.setCaregiverName(notice.getCaregiver() != null ? notice.getCaregiver().getName() : null);
        dto.setPatientName(notice.getPatient() != null ? notice.getPatient().getName() : null);
        dto.setPersonal(notice.isPersonal());
        dto.setPriority(notice.getPriority());
        dto.setPhotoUrl(notice.getPhotoUrl());
        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());
        return dto;
    }
}