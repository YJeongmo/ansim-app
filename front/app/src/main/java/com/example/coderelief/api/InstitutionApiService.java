package com.example.coderelief.api;

import com.example.coderelief.models.Institution;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InstitutionApiService {
    
    // 모든 기관 조회
    @GET("api/institutions")
    Call<List<Institution>> getAllInstitutions();
    
    // ID로 기관 조회
    @GET("api/institutions/{institutionId}")
    Call<Institution> getInstitutionById(@Path("institutionId") Long institutionId);
    
    // 요양원명과 전화번호로 기관 조회
    @GET("api/institutions/search")
    Call<Institution> getInstitutionByNameAndPhone(
        @Query("institutionName") String institutionName,
        @Query("phoneNumber") String phoneNumber
    );
    
    // 요양원명으로 기관 조회
    @GET("api/institutions/search/name")
    Call<List<Institution>> getInstitutionsByName(@Query("institutionName") String institutionName);
}
