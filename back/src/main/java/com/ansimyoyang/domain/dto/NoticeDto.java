package com.ansimyoyang.domain.dto;

import lombok.Data;

@Data
public class NoticeDto {
    private String title;
    private String content;
    private Long institutionId;
    private Long caregiverId;
    private Long patientId; // 개별 공지인 경우에만
    private boolean isPersonal = false;
    private String priority;
    private String photoUrl;
}

