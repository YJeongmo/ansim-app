package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "related_data", columnDefinition = "JSON")
    private String relatedData;

    @Column(name = "is_sent")
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 프론트엔드 호환성을 위한 JSON 직렬화 메서드들
    @JsonProperty("createdAt")
    public Date getCreatedAtAsDate() {
        if (createdAt != null) {
            return Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    @JsonProperty("sentAt")
    public Date getSentAtAsDate() {
        if (sentAt != null) {
            return Date.from(sentAt.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    // 사용자 타입 열거형
    public enum UserType {
        GUARDIAN, CAREGIVER
    }

    // 알림 타입 열거형
    public enum NotificationType {
        CHAT,           // 채팅 메시지
        MEAL,           // 급여 알림
        ACTIVITY,       // 일반 활동
        NOTICE,         // 공지사항
        APPOINTMENT,    // 예약 관련
        CONSULTATION,   // 상담 신청
        HEALTH_ALERT    // 건강상태 주의 알림
    }
}