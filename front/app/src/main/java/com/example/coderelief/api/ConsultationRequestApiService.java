package com.example.coderelief.api;

import com.example.coderelief.models.ConsultationRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConsultationRequestApiService {
    
    // 특정 요양원의 상담 신청 조회
    @GET("api/consultation-requests/institution/{institutionId}")
    Call<List<ConsultationRequest>> getConsultationRequestsByInstitution(@Path("institutionId") Long institutionId);
    
    // ID로 상담 신청 조회
    @GET("api/consultation-requests/{requestId}")
    Call<ConsultationRequest> getConsultationRequestById(@Path("requestId") Long requestId);
    
    // 새로운 상담 신청 생성
    @POST("api/consultation-requests")
    Call<ConsultationRequest> createConsultationRequest(@Body ConsultationRequest request);
    
    // 상담 신청 삭제
    @DELETE("api/consultation-requests/{requestId}")
    Call<Object> deleteConsultationRequest(@Path("requestId") Long requestId);
    
    // 신청자명으로 검색
    @GET("api/consultation-requests/search/applicant")
    Call<List<ConsultationRequest>> searchByApplicantName(@Query("applicantName") String applicantName);
    
    // 통계 정보 조회
    @GET("api/consultation-requests/stats")
    Call<Map<String, Object>> getConsultationRequestStats();
}


