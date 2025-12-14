package com.example.coderelief.api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * 예약 시스템 API 서비스 인터페이스
 */
public interface ReservationApiService {
    
    // 예약 생성
    @POST("api/reservations")
    Call<Map<String, Object>> createReservation(@Body Map<String, Object> reservationRequest);
    
    // 예약 가능 시간 확인
    @GET("api/reservations/availability")
    Call<Map<String, Object>> checkAvailability(
            @Query("institutionId") Long institutionId,
            @Query("reservationDate") String reservationDate,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("requestedVisitors") int requestedVisitors
    );
    
    // 예약 승인/거절
    @PUT("api/reservations/{appointmentId}/approval")
    Call<Map<String, Object>> processApproval(
            @Path("appointmentId") Long appointmentId,
            @Body Map<String, Object> approvalRequest
    );
    
    // 보호자별 예약 목록 조회
    @GET("api/reservations/guardian/{guardianId}")
    Call<List<Map<String, Object>>> getReservationsByGuardian(@Path("guardianId") Long guardianId);
    
    // 환자별 예약 목록 조회
    @GET("api/reservations/patient/{patientId}")
    Call<List<Map<String, Object>>> getReservationsByPatient(@Path("patientId") Long patientId);
    
    // 대기중인 예약 목록 조회 (직원용)
    @GET("api/reservations/pending")
    Call<List<Map<String, Object>>> getPendingReservations();
    
    // 요양원별 예약 목록 조회
    @GET("api/reservations/institution/{institutionId}")
    Call<List<Map<String, Object>>> getReservationsByInstitution(
            @Path("institutionId") Long institutionId,
            @Query("status") String status,
            @Query("date") String date
    );
    
    // 예약 취소
    @PUT("api/reservations/{appointmentId}/cancel")
    Call<Map<String, Object>> cancelReservation(
            @Path("appointmentId") Long appointmentId,
            @Query("reason") String reason
    );
}