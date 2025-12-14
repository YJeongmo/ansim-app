package com.example.coderelief.api;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HealthAnalysisApiService {
    
    /**
     * 환자의 건강상태 분석
     * @param patientId 환자 ID
     * @param days 분석할 일수
     * @return 건강상태 분석 결과
     */
    @GET("api/health-analysis/patient/{patientId}")
    Call<Map<String, Object>> analyzePatientHealth(
            @Path("patientId") Long patientId,
            @Query("days") int days
    );
    
    /**
     * 테스트용 건강상태 분석
     * @return 테스트 분석 결과
     */
    @GET("api/health-analysis/test")
    Call<Map<String, Object>> testHealthAnalysis();
}
