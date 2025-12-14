package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.R;
import com.example.coderelief.models.Activity;
import com.example.coderelief.api.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaregiverActivityRecordFragment extends Fragment {
    
    private TextView tvTitle, tvPatientName;
    private TextInputEditText etActivityTitle, etActivityTime, etActivityDescription, etParticipationLevel, etPhotoDescription;
    private Button btnPhotoCapture, btnPhotoGallery, btnSave;
    
    private String patientName;
    private Long patientId;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caregiver_activity_record, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvPatientName = view.findViewById(R.id.tv_patient_name);
        etActivityTitle = view.findViewById(R.id.et_activity_title);
        etActivityTime = view.findViewById(R.id.et_activity_time);
        etActivityDescription = view.findViewById(R.id.et_activity_description);
        etParticipationLevel = view.findViewById(R.id.et_participation_level);
        etPhotoDescription = view.findViewById(R.id.et_photo_description);
        btnPhotoCapture = view.findViewById(R.id.btn_photo_capture);
        btnPhotoGallery = view.findViewById(R.id.btn_photo_gallery);
        btnSave = view.findViewById(R.id.btn_save);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "환자");
            patientId = args.getLong("patient_id", 0L);
        } else {
            patientName = "환자";
            patientId = 0L;
        }
        
        tvTitle.setText("활동 프로그램 입력");
        tvPatientName.setText(patientName + " 환자 활동 프로그램");
    }
    
    private void setupClickListeners() {
        btnPhotoCapture.setOnClickListener(v -> {
            Toast.makeText(getContext(), "사진 촬영 기능은 준비 중입니다", Toast.LENGTH_SHORT).show();
        });
        
        btnPhotoGallery.setOnClickListener(v -> {
            Toast.makeText(getContext(), "갤러리 선택 기능은 준비 중입니다", Toast.LENGTH_SHORT).show();
        });
        
        btnSave.setOnClickListener(v -> {
            String activityTitle = etActivityTitle.getText() != null ? etActivityTitle.getText().toString().trim() : "";
            String activityTime = etActivityTime.getText() != null ? etActivityTime.getText().toString().trim() : "";
            String activityDescription = etActivityDescription.getText() != null ? etActivityDescription.getText().toString().trim() : "";
            String participationLevel = etParticipationLevel.getText() != null ? etParticipationLevel.getText().toString().trim() : "";
            String photoDescription = etPhotoDescription.getText() != null ? etPhotoDescription.getText().toString().trim() : "";
            
            // Validate required fields
            if (activityTitle.isEmpty() || activityTime.isEmpty()) {
                Toast.makeText(getContext(), "활동 제목과 시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save activity record (TODO: implement actual saving)
            saveActivityRecord(activityTitle, activityTime, activityDescription, participationLevel, photoDescription);
        });
    }
    
    private void saveActivityRecord(String activityTitle, String activityTime, String activityDescription, 
                                  String participationLevel, String photoDescription) {
        if (patientId == null || patientId == 0L) {
            Toast.makeText(getContext(), "환자 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // API를 통해 활동 기록 저장
        saveActivityToApi(activityTitle, activityTime, activityDescription, participationLevel, photoDescription);
    }
    
    private void saveActivityToApi(String activityTitle, String activityTime, String activityDescription, 
                                 String participationLevel, String photoDescription) {
        // TODO: 실제 구현에서는 요양보호사 ID를 로그인 정보에서 가져와야 함
        Long caregiverId = 1L; // 임시 값
        
        // 백엔드가 기대하는 형식으로 데이터 생성
        java.util.Map<String, Object> activityData = new java.util.HashMap<>();
        activityData.put("patientId", patientId);
        activityData.put("caregiverId", caregiverId);
        activityData.put("type", activityTitle);
        activityData.put("description", activityDescription);
        activityData.put("photoUrl", photoDescription); // 임시로 photoDescription을 photoUrl에 저장
        activityData.put("activityTime", new java.util.Date().toString()); // 현재 시간을 String으로 전송
        
        // API 호출하여 저장
        ApiClient.getApiService().saveActivity(activityData)
                .enqueue(new Callback<java.util.Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<java.util.Map<String, Object>> call, Response<java.util.Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            java.util.Map<String, Object> result = response.body();
                            Boolean success = (Boolean) result.get("success");
                            
                            if (success != null && success) {
                                String message = (String) result.get("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                
                                // 이전 화면으로 돌아가기
                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            } else {
                                String errorMessage = (String) result.get("message");
                                Toast.makeText(getContext(), errorMessage != null ? errorMessage : "저장에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "저장에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                        Toast.makeText(getContext(), "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}