package com.example.coderelief.api;

import com.example.coderelief.models.Patient;
import com.example.coderelief.models.Activity;
import com.example.coderelief.models.DailyRecord;
import com.example.coderelief.models.Institution;
import com.example.coderelief.models.Notice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;

/**
 * Spring Boot 백엔드와 통신하기 위한 API 서비스 인터페이스
 */
public interface ApiService {
    
    // 환자 관련 API
    @GET("api/patients")
    Call<List<Patient>> getPatients();
    
    @GET("api/patients/institution/{institutionId}")
    Call<List<Patient>> getPatientsByInstitution(@Path("institutionId") Long institutionId);
    
    @GET("api/patients/{patientId}")
    Call<Patient> getPatientById(@Path("patientId") Long patientId);
    
    // 보호자별 환자 정보 조회
    @GET("api/patients/guardian/{guardianId}")
    Call<Patient> getPatientByGuardian(@Path("guardianId") Long guardianId);
    
    // 활동 기록 관련 API
    @GET("api/activities/patient")
    Call<List<Activity>> getActivitiesByPatient(@Query("patientId") Long patientId);
    
    @GET("api/activities/recent")
    Call<List<Activity>> getRecentActivities(@Query("patientId") Long patientId, @Query("limit") int limit);
    
    // 활동 기록 저장 API
    @POST("api/activities")
    Call<java.util.Map<String, Object>> saveActivity(@Body java.util.Map<String, Object> activityData);
    
    // 데일리 기록 관련 API
    @GET("api/daily-records/patient/{patientId}")
    Call<List<DailyRecord>> getDailyRecordsByPatient(@Path("patientId") Long patientId);
    
    @GET("api/daily-records/date")
    Call<List<DailyRecord>> getDailyRecordsByDate(@Query("date") String date);
    
    // 데일리 기록 저장 API
    @POST("api/daily-records")
    Call<DailyRecord> saveDailyRecord(@Body DailyRecord dailyRecord);
    
    // 기관 관련 API
    @GET("api/institutions")
    Call<List<Institution>> getInstitutions();
    
    // 공지사항 관련 API
    @GET("api/notices")
    Call<java.util.Map<String, Object>> getAllNotices();
    
    @GET("api/notices/{noticeId}")
    Call<java.util.Map<String, Object>> getNoticeById(@Path("noticeId") Long noticeId);
    
    @GET("api/notices/institution/{institutionId}")
    Call<java.util.Map<String, Object>> getNoticesByInstitution(
            @Path("institutionId") Long institutionId,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("direction") String direction);
    
    @GET("api/notices/institution/{institutionId}/personal/{patientId}")
    Call<java.util.Map<String, Object>> getPersonalNoticesByInstitution(
            @Path("institutionId") Long institutionId,
            @Path("patientId") Long patientId);
    
    @GET("api/notices/patient/{patientId}")
    Call<java.util.Map<String, Object>> getPersonalNoticesByPatient(@Path("patientId") Long patientId);
    
    @GET("api/notices/caregiver/{caregiverId}")
    Call<java.util.Map<String, Object>> getNoticesByCaregiver(@Path("caregiverId") Long caregiverId);
    
    @GET("api/notices/search")
    Call<java.util.Map<String, Object>> searchNoticesByTitle(
            @Query("institutionId") Long institutionId,
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size);
    
    @GET("api/notices/recent")
    Call<java.util.Map<String, Object>> getRecentNotices(
            @Query("institutionId") Long institutionId,
            @Query("limit") int limit);
    
    @POST("api/notices")
    Call<java.util.Map<String, Object>> createNotice(@Body java.util.Map<String, Object> noticeData);
    
    @PUT("api/notices/{noticeId}")
    Call<java.util.Map<String, Object>> updateNotice(
            @Path("noticeId") Long noticeId,
            @Body java.util.Map<String, Object> noticeData);
    
    @DELETE("api/notices/{noticeId}")
    Call<java.util.Map<String, Object>> deleteNotice(@Path("noticeId") Long noticeId);
    
    @GET("api/notices/test")
    Call<String> testNoticeApi();
}
