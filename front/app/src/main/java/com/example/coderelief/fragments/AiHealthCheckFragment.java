package com.example.coderelief.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.HealthAnalysisApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiHealthCheckFragment extends Fragment {
    
    private TextView tvTitle, tvPatientInfo, tvAnalysisStatus;
    private ProgressBar progressAnalysis;
    private com.google.android.material.card.MaterialCardView cardAnalysisResult;
    private TextView tvOverallAssessment, tvConcerns, tvRecommendations, tvDetailedAnalysis;
    private Button btnAnalyzeAgain, btnBack;
    
    private String patientName;
    private Long patientId;
    private String userRole;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai_health_check, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        setupClickListeners();
        startHealthAnalysis();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvPatientInfo = view.findViewById(R.id.tv_patient_info);
        tvAnalysisStatus = view.findViewById(R.id.tv_analysis_status);
        progressAnalysis = view.findViewById(R.id.progress_analysis);
        cardAnalysisResult = view.findViewById(R.id.card_analysis_result);
        tvOverallAssessment = view.findViewById(R.id.tv_overall_assessment);
        tvConcerns = view.findViewById(R.id.tv_concerns);
        tvRecommendations = view.findViewById(R.id.tv_recommendations);
        tvDetailedAnalysis = view.findViewById(R.id.tv_detailed_analysis);
        btnAnalyzeAgain = view.findViewById(R.id.btn_analyze_again);
        btnBack = view.findViewById(R.id.btn_back);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "환자");
            patientId = args.getLong("patient_id", 0L);
            userRole = args.getString("user_role", "guardian");
        } else {
            patientName = "환자";
            patientId = 0L;
            userRole = "guardian";
        }
        
        // 환자 정보 표시
        String patientInfoText = String.format("환자: %s\n분석 기간: 최근 5일간", patientName);
        tvPatientInfo.setText(patientInfoText);
    }
    
    private void setupClickListeners() {
        btnAnalyzeAgain.setOnClickListener(v -> {
            startHealthAnalysis();
        });
        
        btnBack.setOnClickListener(v -> {
            // 이전 화면으로 돌아가기
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }
    
    private void startHealthAnalysis() {
        if (patientId == null || patientId == 0L) {
            Toast.makeText(getContext(), "환자 정보를 확인할 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 분석 시작 UI
        showAnalysisProgress();
        
        // API 호출
        callHealthAnalysisApi();
    }
    
    private void showAnalysisProgress() {
        progressAnalysis.setVisibility(View.VISIBLE);
        cardAnalysisResult.setVisibility(View.GONE);
        tvAnalysisStatus.setText("AI가 건강상태를 분석하고 있습니다...");
        
        // 프로그레스바 애니메이션
        progressAnalysis.setProgress(0);
        animateProgress();
    }
    
    private void animateProgress() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            int progress = 0;
            @Override
            public void run() {
                if (progress <= 90) {
                    progressAnalysis.setProgress(progress);
                    progress += 10;
                    handler.postDelayed(this, 200);
                }
            }
        };
        handler.post(runnable);
    }
    
    private void callHealthAnalysisApi() {
        HealthAnalysisApiService apiService = ApiClient.getHealthAnalysisApiService();
        Call<Map<String, Object>> call = apiService.analyzePatientHealth(patientId, 5);
        
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    boolean success = (Boolean) responseBody.get("success");
                    
                    if (success) {
                        displayAnalysisResult(responseBody);
                    } else {
                        showAnalysisError((String) responseBody.get("message"));
                    }
                } else {
                    showAnalysisError("서버 오류가 발생했습니다.");
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showAnalysisError("네트워크 오류가 발생했습니다: " + t.getMessage());
            }
        });
    }
    
    private void displayAnalysisResult(Map<String, Object> responseBody) {
        // 프로그레스바 완료
        progressAnalysis.setProgress(100);
        
        // 분석 완료 상태 표시
        tvAnalysisStatus.setText("✅ 분석이 완료되었습니다!");
        
        // 결과 데이터 파싱
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        if (data != null) {
            Map<String, Object> analysisResult = (Map<String, Object>) data.get("analysisResult");
            if (analysisResult != null) {
                // 전체 평가
                String overallAssessment = (String) analysisResult.get("overallAssessment");
                tvOverallAssessment.setText(overallAssessment != null ? overallAssessment : "평가 정보 없음");
                
                // 우려사항
                List<String> concerns = (List<String>) analysisResult.get("concerns");
                if (concerns != null && !concerns.isEmpty()) {
                    StringBuilder concernsText = new StringBuilder();
                    for (String concern : concerns) {
                        concernsText.append("• ").append(concern).append("\n");
                    }
                    tvConcerns.setText(concernsText.toString().trim());
                } else {
                    tvConcerns.setText("특별한 우려사항이 없습니다.");
                }
                
                // 권장사항
                List<String> recommendations = (List<String>) analysisResult.get("recommendations");
                if (recommendations != null && !recommendations.isEmpty()) {
                    StringBuilder recommendationsText = new StringBuilder();
                    for (String recommendation : recommendations) {
                        recommendationsText.append("• ").append(recommendation).append("\n");
                    }
                    tvRecommendations.setText(recommendationsText.toString().trim());
                } else {
                    tvRecommendations.setText("특별한 권장사항이 없습니다.");
                }
                
                // 상세 분석
                String detailedAnalysis = (String) analysisResult.get("detailedAnalysis");
                tvDetailedAnalysis.setText(detailedAnalysis != null ? detailedAnalysis : "상세 분석 정보가 없습니다.");
            }
        }
        
        // 결과 카드 표시
        cardAnalysisResult.setVisibility(View.VISIBLE);
        progressAnalysis.setVisibility(View.GONE);
    }
    
    private void showAnalysisError(String errorMessage) {
        progressAnalysis.setVisibility(View.GONE);
        tvAnalysisStatus.setText("❌ 분석 실패: " + errorMessage);
        
        // 에러 메시지 표시
        Toast.makeText(getContext(), "건강상태 분석에 실패했습니다: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}
