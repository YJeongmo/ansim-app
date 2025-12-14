package com.ansimyoyang.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ActivityDto {
    private Long patientId;
    private Long caregiverId;
    private String type;
    private String description;
    private String photoUrl;
    private String activityTime; // LocalDateTime에서 String으로 변경
    
    // String을 LocalDateTime으로 변환하는 메서드
    public LocalDateTime getActivityTimeAsLocalDateTime() {
        if (activityTime == null || activityTime.trim().isEmpty()) {
            return LocalDateTime.now(); // 기본값으로 현재 시간 사용
        }
        
        try {
            // 다양한 날짜 형식 시도
            String[] patterns = {
                "MMM dd, yyyy h:mm:ss a", // "Aug 28, 2025 7:32:35 AM"
                "yyyy-MM-dd HH:mm:ss",    // "2025-08-28 07:32:35"
                "yyyy-MM-dd'T'HH:mm:ss",  // "2025-08-28T07:32:35"
                "MM/dd/yyyy HH:mm:ss",    // "08/28/2025 07:32:35"
                "dd/MM/yyyy HH:mm:ss"     // "28/08/2025 07:32:35"
            };
            
            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    if (pattern.contains("a")) {
                        // AM/PM 형식 처리
                        return LocalDateTime.parse(activityTime, formatter);
                    } else {
                        return LocalDateTime.parse(activityTime, formatter);
                    }
                } catch (Exception e) {
                    // 이 패턴으로 파싱 실패, 다음 패턴 시도
                    continue;
                }
            }
            
            // 모든 패턴 실패 시 현재 시간 반환
            return LocalDateTime.now();
            
        } catch (Exception e) {
            // 파싱 실패 시 현재 시간 반환
            return LocalDateTime.now();
        }
    }
}

