package com.ansimyoyang.service;

import com.ansimyoyang.domain.Notification;
import com.ansimyoyang.domain.Notification.UserType;
import com.ansimyoyang.domain.Notification.NotificationType;
import com.ansimyoyang.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 생성
    public Notification createNotification(Long userId, UserType userType, String title, 
                                         String message, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .userId(userId)
                .userType(userType)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .isSent(false)
                .build();

        return notificationRepository.save(notification);
    }

    // 관련 ID와 함께 알림 생성
    public Notification createNotificationWithRelatedId(Long userId, UserType userType, String title,
                                                       String message, NotificationType notificationType,
                                                       Long relatedId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .userType(userType)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .relatedId(relatedId)
                .isSent(false)
                .build();

        return notificationRepository.save(notification);
    }

    // 사용자별 알림 목록 조회
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(Long userId, UserType userType) {
        return notificationRepository.findByUserIdAndUserTypeOrderByCreatedAtDesc(userId, userType);
    }

    // 특정 타입의 알림 조회
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(Long userId, UserType userType, NotificationType notificationType) {
        return notificationRepository.findByUserIdAndUserTypeAndNotificationTypeOrderByCreatedAtDesc(
                userId, userType, notificationType);
    }

    // 채팅 메시지 알림 생성
    public Notification createChatNotification(Long recipientId, UserType recipientType, 
                                             String senderName, Long chatRoomId) {
        String title = "새로운 채팅 메시지";
        String message = senderName + "님이 메시지를 보냈습니다.";
        
        return createNotificationWithRelatedId(recipientId, recipientType, title, message, 
                                             NotificationType.CHAT, chatRoomId);
    }

    // 급여 알림 생성 (보호자용)
    public Notification createMealNotification(Long guardianId, String patientName, String mealType) {
        String title = "급식 알림";
        String message = patientName + "님이 " + mealType + "을(를) 드셨습니다.";
        
        return createNotification(guardianId, UserType.GUARDIAN, title, message, NotificationType.MEAL);
    }

    // 활동 알림 생성 (보호자용)
    public Notification createActivityNotification(Long guardianId, String patientName, 
                                                  String activityType, Long activityId) {
        String title = "활동 알림";
        String message = patientName + "님이 " + activityType + " 활동을 하셨습니다.";
        
        return createNotificationWithRelatedId(guardianId, UserType.GUARDIAN, title, message, 
                                             NotificationType.ACTIVITY, activityId);
    }

    // 공지사항 알림 생성
    public Notification createNoticeNotification(Long userId, UserType userType, String noticeTitle, Long noticeId) {
        String title = "새로운 공지사항";
        String message = "새 공지사항: " + noticeTitle;
        
        return createNotificationWithRelatedId(userId, userType, title, message, 
                                             NotificationType.NOTICE, noticeId);
    }

    // 예약 관련 알림 생성
    public Notification createAppointmentNotification(Long userId, UserType userType, 
                                                    String appointmentType, String status, Long appointmentId) {
        String title = appointmentType + " 알림";
        String message;
        
        switch (status) {
            case "APPROVED":
                message = appointmentType + " 신청이 승인되었습니다.";
                break;
            case "REJECTED":
                message = appointmentType + " 신청이 거부되었습니다.";
                break;
            case "PENDING":
                message = "새로운 " + appointmentType + " 신청이 있습니다.";
                break;
            default:
                message = appointmentType + " 상태가 변경되었습니다.";
        }
        
        return createNotificationWithRelatedId(userId, userType, title, message, 
                                             NotificationType.APPOINTMENT, appointmentId);
    }

    // 상담 신청 알림 생성 (요양보호사용)
    public Notification createConsultationNotification(Long caregiverId, String applicantName, Long consultationId) {
        String title = "새로운 상담 신청";
        String message = applicantName + "님이 상담을 신청했습니다.";
        
        return createNotificationWithRelatedId(caregiverId, UserType.CAREGIVER, title, message, 
                                             NotificationType.CONSULTATION, consultationId);
    }

    // 미발송 알림 조회 (FCM 발송용)
    @Transactional(readOnly = true)
    public List<Notification> getUnsentNotifications() {
        return notificationRepository.findUnsentNotifications();
    }

    // 알림 발송 상태 업데이트
    public void markAsSent(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));
        
        notification.setIsSent(true);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // 알림 개수 조회
    @Transactional(readOnly = true)
    public Long getRecentNotificationCount(Long userId, UserType userType, int hours) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(hours);
        return notificationRepository.countRecentNotifications(userId, userType, startDate);
    }

    // 알림을 읽음 처리
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + notificationId));
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // 여러 알림을 한번에 읽음 처리
    public void markAllAsRead(Long userId, UserType userType) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadNotifications(userId, userType);
        
        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(now);
        }
        
        notificationRepository.saveAll(unreadNotifications);
    }

    // 미읽은 알림 개수 조회
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userId, UserType userType) {
        return notificationRepository.countUnreadNotifications(userId, userType);
    }
}