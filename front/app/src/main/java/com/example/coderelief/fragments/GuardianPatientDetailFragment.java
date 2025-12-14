package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.models.Patient;
import com.example.coderelief.models.Activity;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.dialogs.ReservationRequestDialog;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 보호자 전용 가족(환자) 상세 정보 Fragment
 * coderelief1의 GuardianPatientDetailScreenPreview를 참고하여 구현
 * 보호자가 자신의 가족 정보를 확인하는 화면
 */
public class GuardianPatientDetailFragment extends Fragment {
    
    private TextView tvTitle, tvCareCenterInfo;
    private TextView tvFamilyNotices;
    private LinearLayout btnTodayMeal, btnTodayActivity, btnAiHealthCheck;
    
    private String patientName;
    private Long patientId;
    private String userRole;
    private String careCenterName;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guardian_patient_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        loadFamilyInfo();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvCareCenterInfo = view.findViewById(R.id.tv_care_center_info);
        tvFamilyNotices = view.findViewById(R.id.tv_family_notices);
        btnTodayMeal = view.findViewById(R.id.btn_today_meal);
        btnTodayActivity = view.findViewById(R.id.btn_today_activity);
        btnAiHealthCheck = view.findViewById(R.id.btn_ai_health_check);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "가족");
            patientId = args.getLong("patient_id", 0L);
            userRole = args.getString("user_role", "guardian");
            careCenterName = args.getString("care_center_name", "요양원");
        } else {
            patientName = "가족";
            userRole = "guardian";
            careCenterName = "요양원";
        }
    }
    
    private void loadFamilyInfo() {
        tvTitle.setText("가족 상세 정보");
        
        // API에서 실제 데이터 로드
        loadFamilyInfoFromApi();
    }
    
    private void loadFamilyInfoFromApi() {
        if (patientId != null && patientId > 0) {
            // 환자 정보 로드
            loadPatientInfo();
            // 최근 활동 사진 로드
            loadRecentActivities();
        } else {
            // patientId가 없는 경우 폴백 데이터 사용
            loadFallbackData();
        }
    }
    
    private void loadPatientInfo() {
        ApiClient.getApiService().getPatientById(patientId)
                .enqueue(new Callback<Patient>() {
                    @Override
                    public void onResponse(Call<Patient> call, Response<Patient> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Patient patient = response.body();
                            displayPatientInfo(patient);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Patient> call, Throwable t) {
                        // 실패 시 폴백 데이터 사용
                    }
                });
    }
    
    private void loadRecentActivities() {
        ApiClient.getApiService().getRecentActivities(patientId, 5)
                .enqueue(new Callback<List<Activity>>() {
                    @Override
                    public void onResponse(Call<List<Activity>> call, Response<List<Activity>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Activity> activities = response.body();
                            displayRecentActivities(activities);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<Activity>> call, Throwable t) {
                        // 실패 시 폴백 데이터 사용
                    }
                });
    }
    
    private void displayPatientInfo(Patient patient) {
        String familyInfo = String.format(
            "나이: %d세\n방호실: %s\n요양등급: %s\n입소일: %s\n관계: %s",
            patient.getAge() != null ? patient.getAge() : 0,
            patient.getRoomNumber() != null ? patient.getRoomNumber() : "-",
            patient.getCareLevel() != null ? patient.getCareLevel() : "-",
            patient.getAdmissionDate() != null ? patient.getAdmissionDate().toString() : "-",
            "가족"
        );
        
        String careCenterInfo = String.format(
            "요양원명: %s\n주소: -\n연락처: -\n담당간호사: %s",
            careCenterName,
            "-"
        );
        
        tvCareCenterInfo.setText(careCenterInfo);
    }
    
    private void displayRecentActivities(List<Activity> activities) {
        StringBuilder recentPhotos = new StringBuilder();
        
        if (activities.isEmpty()) {
            recentPhotos.append("📸 최근 활동 기록이 없습니다\n\n");
        } else {
            for (Activity activity : activities) {
                recentPhotos.append(String.format("📸 %s: %s\n", 
                    activity.getActivityTime() != null ? activity.getActivityTime().toString() : "시간 미정",
                    activity.getDescription() != null ? activity.getDescription() : "활동 내용"
                ));
                }
            }
        
    }
    
    private void loadFallbackData() {
        // 하드코딩된 데이터 제거 - 기본 메시지만 표시
        String careCenterInfo = "요양원 정보를 불러올 수 없습니다.";
        tvCareCenterInfo.setText(careCenterInfo);
    }
    
    private void setupClickListeners() {
        // 오늘의 급여 확인 버튼
        btnTodayMeal.setOnClickListener(v -> {
            PatientDetailFragment fragment = new PatientDetailFragment();
            Bundle args = new Bundle();
            args.putString("patient_name", patientName);
            args.putString("user_role", userRole);
            args.putString("detail_type", "meal"); // 급여 정보 타입
            args.putLong("patient_id", patientId);
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        // 오늘의 활동 확인 버튼
        btnTodayActivity.setOnClickListener(v -> {
            PatientDetailFragment fragment = new PatientDetailFragment();
            Bundle args = new Bundle();
            args.putString("patient_name", patientName);
            args.putString("user_role", userRole);
            args.putString("detail_type", "activity"); // 활동 정보 타입
            args.putLong("patient_id", patientId);
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        
        // 보호자 로그인 시에는 AI 건강 체크 버튼 숨기기
        if ("guardian".equals(userRole)) {
            btnAiHealthCheck.setVisibility(View.GONE);
        } else {
            // 간병인 로그인 시에만 AI 건강 체크 버튼 활성화
            btnAiHealthCheck.setOnClickListener(v -> {
                showAiHealthCheckDialog();
            });
        }
    }
    
    private void showReservationDialog() {
        Bundle args = getArguments();
        if (args == null) {
            android.widget.Toast.makeText(getContext(), "예약에 필요한 정보가 부족합니다", 
                android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        Long guardianId = args.getLong("guardian_id", 0L);
        if (guardianId == 0L) {
            android.widget.Toast.makeText(getContext(), "보호자 정보를 확인할 수 없습니다", 
                android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        ReservationRequestDialog dialog = ReservationRequestDialog.newInstance(guardianId);
        dialog.show(getParentFragmentManager(), "ReservationRequestDialog");
    }
    
    private void showAiHealthCheckDialog() {
        if (patientId == null || patientId == 0L) {
            android.widget.Toast.makeText(getContext(), "환자 정보를 확인할 수 없습니다", 
                android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        // AI 건강 체크 Fragment로 이동
        AiHealthCheckFragment fragment = new AiHealthCheckFragment();
        Bundle args = new Bundle();
        args.putLong("patient_id", patientId);
        args.putString("patient_name", patientName);
        args.putString("user_role", userRole);
        fragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
}