package com.example.coderelief.api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 알림 API 서비스 인터페이스
 * 서버의 NotificationController와 매칭
 */
public interface NotificationApiService {

    /**
     * 사용자별 알림 목록 조회
     * GET /api/notifications/{userId}?userType=GUARDIAN
     */
    @GET("api/notifications/{userId}")
    Call<Map<String, Object>> getNotifications(
        @Path("userId") Long userId,
        @Query("userType") String userType
    );

    /**
     * 특정 타입의 알림 조회
     * GET /api/notifications/{userId}/type/{notificationType}?userType=GUARDIAN
     */
    @GET("api/notifications/{userId}/type/{notificationType}")
    Call<Map<String, Object>> getNotificationsByType(
        @Path("userId") Long userId,
        @Path("notificationType") String notificationType,
        @Query("userType") String userType
    );

    /**
     * 최근 알림 개수 조회
     * GET /api/notifications/{userId}/count?userType=GUARDIAN&hours=24
     */
    @GET("api/notifications/{userId}/count")
    Call<Map<String, Object>> getNotificationCount(
        @Path("userId") Long userId,
        @Query("userType") String userType,
        @Query("hours") Integer hours
    );

    /**
     * 테스트 알림 생성
     * POST /api/notifications/test
     */
    @POST("api/notifications/test")
    Call<Map<String, Object>> createTestNotification(
        @Query("userId") Long userId,
        @Query("userType") String userType,
        @Query("title") String title,
        @Query("message") String message,
        @Query("notificationType") String notificationType
    );

    /**
     * 미발송 알림 조회 (관리자용)
     * GET /api/notifications/unsent
     */
    @GET("api/notifications/unsent")
    Call<Map<String, Object>> getUnsentNotifications();

    /**
     * 알림 발송 상태 업데이트
     * PATCH /api/notifications/{notificationId}/sent
     */
    @PATCH("api/notifications/{notificationId}/sent")
    Call<Map<String, Object>> markAsSent(@Path("notificationId") Long notificationId);

    /**
     * 알림 읽음 처리
     * PATCH /api/notifications/{notificationId}/read
     */
    @PATCH("api/notifications/{notificationId}/read")
    Call<Map<String, Object>> markAsRead(@Path("notificationId") Long notificationId);

    /**
     * 모든 알림 읽음 처리
     * PATCH /api/notifications/read-all?userId={userId}&userType={userType}
     */
    @PATCH("api/notifications/read-all")
    Call<Map<String, Object>> markAllAsRead(
        @Query("userId") Long userId,
        @Query("userType") String userType
    );

    /**
     * 미읽은 알림 개수 조회
     * GET /api/notifications/{userId}/unread-count?userType=GUARDIAN
     */
    @GET("api/notifications/{userId}/unread-count")
    Call<Map<String, Object>> getUnreadNotificationCount(
        @Path("userId") Long userId,
        @Query("userType") String userType
    );
}