package com.ansimyoyang.domain.dto;

import com.ansimyoyang.domain.DailyRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRecordDto {

    private Long patientId;
    private Long caregiverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;
    
    // 문자열로 받은 날짜를 LocalDate로 변환하기 위한 setter
    public void setRecordDate(String recordDate) {
        if (recordDate != null && !recordDate.isEmpty()) {
            this.recordDate = LocalDate.parse(recordDate);
        }
    }

    private DailyRecord.TimeSlot timeSlot;  // BREAKFAST / LUNCH / DINNER
    private DailyRecord.Level meal;         // GOOD / NORMAL / BAD
    private DailyRecord.Level condition;    // GOOD / NORMAL / BAD

    private Boolean medicationTaken;        // null이면 Service에서 false 처리 가능
    private String notes;
}
