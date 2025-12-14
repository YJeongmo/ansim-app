package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Notification;
import com.ansimyoyang.domain.Notification.UserType;
import com.ansimyoyang.domain.Notification.NotificationType;
import com.ansimyoyang.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // 사용자별 알림 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getNotifications(
            @PathVariable Long userId,
            @RequestParam String userType) {
        try {
            log.info("알림 목록 조회 요청: userId={}, userType={}", userId, userType);
            
            UserType type = UserType.valueOf(userType.toUpperCase());
            List<Notification> notifications = notificationService.getNotificationsByUser(userId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notifications);
            response.put("count", notifications.size());
            
            log.info("알림 목록 조회 성공: count={}", notifications.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("알림 목록 조회 실패: userId={}, userType={}, error={}", userId, userType, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "알림 목록을 불러올 수 없습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 특정 타입의 알림 조회
    @GetMapping("/{userId}/type/{notificationType}")
    public ResponseEntity<Map<String, Object>> getNotificationsByType(
            @PathVariable Long userId,
            @PathVariable String notificationType,
            @RequestParam String userType) {
        try {
            log.info("타입별 알림 조회 요청: userId={}, userType={}, notificationType={}", 
                    userId, userType, notificationType);
            
            UserType type = UserType.valueOf(userType.toUpperCase());
            NotificationType notiType = NotificationType.valueOf(notificationType.toUpperCase());
            
            List<Notification> notifications = notificationService.getNotificationsByType(userId, type, notiType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notifications);
            response.put("count", notifications.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("타입별 알림 조회 실패: error={}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "알림을 불러올 수 없습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 최근 알림 개수 조회
    @GetMapping("/{userId}/count")
    public ResponseEntity<Map<String, Object>> getRecentNotificationCount(
            @PathVariable Long userId,
            @RequestParam String userType,
            @RequestParam(defaultValue = "24") int hours) {
        try {
            log.info("최근 알림 개수 조회: userId={}, userType={}, hours={}", userId, userType, hours);
            
            UserType type = UserType.valueOf(userType.toUpperCase());
            Long count = notificationService.getRecentNotificationCount(userId, type, hours);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("알림 개수 조회 실패: error={}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("count", 0);
            
            return ResponseEntity.ok(response);
        }
    }

    // 알림 생성 (테스트용)
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> createTestNotification(
            @RequestParam Long userId,
            @RequestParam String userType,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String notificationType) {
        try {
            log.info("테스트 알림 생성 요청: userId={}, userType={}, type={}", userId, userType, notificationType);
            
            UserType type = UserType.valueOf(userType.toUpperCase());
            NotificationType notiType = NotificationType.valueOf(notificationType.toUpperCase());
            
            Notification notification = notificationService.createNotification(userId, type, title, message, notiType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notification);
            response.put("message", "테스트 알림이 생성되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("테스트 알림 생성 실패: error={}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "알림 생성에 실패했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 미발송 알림 조회 (FCM 발송용)
    @GetMapping("/unsent")
    public ResponseEntity<Map<String, Object>> getUnsentNotifications() {
        try {
            log.info("미발송 알림 조회 요청");
            
            List<Notification> notifications = notificationService.getUnsentNotifications();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notifications);
            response.put("count", notifications.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("미발송 알림 조회 실패: error={}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "미발송 알림을 불러올 수 없습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 알림 발송 상태 업데이트
    @PatchMapping("/{notificationId}/sent")
    public ResponseEntity<Map<String, Object>> markAsSent(@PathVariable Long notificationId) {
        try {
            log.info("알림 발송 상태 업데이트: notificationId={}", notificationId);
            
            notificationService.markAsSent(notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "알림 발송 상태가 업데이트되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("알림 발송 상태 업데이트 실패: notificationId={}, error={}", notificationId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "상태 업데이트에 실패했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long notificationId) {
        try {
            log.info("알림 읽음 처리: notificationId={}", notificationId);
            
            notificationService.markAsRead(notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "알림을 읽음 처리했습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: notificationId={}, error={}", notificationId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "읽음 처리에 실패했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 모든 알림 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @RequestParam Long userId,
            @RequestParam String userType) {
        try {
            log.info("모든 알림 읽음 처리: userId={}, userType={}", userId, userType);
            
            UserType type = UserType.valueOf(userType.toUpperCase());
            notificationService.markAllAsRead(userId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "모든 알림을 읽음 처리했습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("모든 알림 읽음 처리 실패: userId={}, userType={}, error={}", userId, userType, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "읽음 처리에 실패했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 미읽은 알림 개수 조회
    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadNotificationCount(
            @PathVariable Long userId,
            @RequestParam String userType) {
        try {
            log.info("미읽은 알림 개수 조회: userId={}, userType={}", userId, userType);
            
            UserType type = UserType.valueOf(userType.toUpperCase());
            Long count = notificationService.getUnreadNotificationCount(userId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("미읽은 알림 개수 조회 실패: error={}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("count", 0);
            
            return ResponseEntity.ok(response);
        }
    }
}