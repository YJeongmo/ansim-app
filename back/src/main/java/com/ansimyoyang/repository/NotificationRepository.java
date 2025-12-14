package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Notification;
import com.ansimyoyang.domain.Notification.UserType;
import com.ansimyoyang.domain.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 사용자별 알림 목록 조회 (최신순)
    List<Notification> findByUserIdAndUserTypeOrderByCreatedAtDesc(Long userId, UserType userType);

    // 특정 타입의 알림 조회
    List<Notification> findByUserIdAndUserTypeAndNotificationTypeOrderByCreatedAtDesc(
            Long userId, UserType userType, NotificationType notificationType);

    // 관련 ID로 알림 조회 (예: 특정 채팅방의 모든 알림)
    List<Notification> findByRelatedIdAndNotificationTypeOrderByCreatedAtDesc(
            Long relatedId, NotificationType notificationType);

    // 미발송 알림 조회 (FCM 발송용)
    @Query("SELECT n FROM Notification n WHERE n.isSent = false ORDER BY n.createdAt ASC")
    List<Notification> findUnsentNotifications();

    // 특정 기간 내 알림 개수 조회
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.userType = :userType " +
           "AND n.createdAt >= :startDate")
    Long countRecentNotifications(@Param("userId") Long userId, 
                                 @Param("userType") UserType userType,
                                 @Param("startDate") java.time.LocalDateTime startDate);

    // 미읽은 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.userType = :userType " +
           "AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotifications(@Param("userId") Long userId, 
                                              @Param("userType") UserType userType);

    // 미읽은 알림 개수 조회
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.userType = :userType " +
           "AND n.isRead = false")
    Long countUnreadNotifications(@Param("userId") Long userId, 
                                 @Param("userType") UserType userType);
}