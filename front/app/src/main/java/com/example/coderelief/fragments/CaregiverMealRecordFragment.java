package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.models.DailyRecord;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaregiverMealRecordFragment extends Fragment {
    
    private TextView tvTitle, tvPatientName;
    private TextInputEditText etBreakfast, etLunch, etDinner, etMedication, etHealthStatus, etSpecialNotes;
    private Button btnSave;
    
    private String patientName;
    private Long patientId;
    private Long caregiverId = 1L; // 임시로 간병인 ID 1 사용
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caregiver_meal_record, container, false);
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
        etBreakfast = view.findViewById(R.id.et_breakfast);
        etLunch = view.findViewById(R.id.et_lunch);
        etDinner = view.findViewById(R.id.et_dinner);
        etMedication = view.findViewById(R.id.et_medication);
        etHealthStatus = view.findViewById(R.id.et_health_status);
        etSpecialNotes = view.findViewById(R.id.et_special_notes);
        btnSave = view.findViewById(R.id.btn_save);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "환자");
            patientId = args.getLong("patient_id", 1L); // 기본값으로 환자 ID 1 사용
        } else {
            patientName = "환자";
            patientId = 1L; // 기본값으로 환자 ID 1 사용
        }
        
        tvTitle.setText("급여 내역 입력");
        tvPatientName.setText(patientName + " 환자 급여 내역");
    }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            String breakfast = etBreakfast.getText() != null ? etBreakfast.getText().toString().trim() : "";
            String lunch = etLunch.getText() != null ? etLunch.getText().toString().trim() : "";
            String dinner = etDinner.getText() != null ? etDinner.getText().toString().trim() : "";
            String medication = etMedication.getText() != null ? etMedication.getText().toString().trim() : "";
            String healthStatus = etHealthStatus.getText() != null ? etHealthStatus.getText().toString().trim() : "";
            String specialNotes = etSpecialNotes.getText() != null ? etSpecialNotes.getText().toString().trim() : "";
            
            // Validate required fields
            if (breakfast.isEmpty() || lunch.isEmpty() || dinner.isEmpty()) {
                Toast.makeText(getContext(), "식사 현황을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save meal record (TODO: implement actual saving)
            saveMealRecord(breakfast, lunch, dinner, medication, healthStatus, specialNotes);
        });
    }
    
    private void saveMealRecord(String breakfast, String lunch, String dinner, 
                              String medication, String healthStatus, String specialNotes) {
        // 하루에 하나의 DailyRecord 생성 (LUNCH 타임슬롯으로 통합)
        saveMealRecordForDay(breakfast, lunch, dinner, medication, healthStatus, specialNotes);
    }
    
    private void saveMealRecordForDay(String breakfast, String lunch, String dinner, 
                                     String medication, String healthStatus, String specialNotes) {
        // 디버깅을 위한 로그 추가
        Log.d("CaregiverMealRecord", "아침 식사: '" + breakfast + "'");
        Log.d("CaregiverMealRecord", "점심 식사: '" + lunch + "'");
        Log.d("CaregiverMealRecord", "저녁 식사: '" + dinner + "'");
        Log.d("CaregiverMealRecord", "투약 입력: '" + medication + "'");
        Log.d("CaregiverMealRecord", "건강상태 입력: '" + healthStatus + "'");
        Log.d("CaregiverMealRecord", "특이사항 입력: '" + specialNotes + "'");
        
        // DailyRecord 객체 생성
        DailyRecord dailyRecord = new DailyRecord();
        dailyRecord.setPatientId(patientId);
        dailyRecord.setCaregiverId(caregiverId);
        dailyRecord.setRecordDate(new Date()); // Date 객체를 yyyy-MM-dd 형식으로 자동 변환
        dailyRecord.setTimeSlot("LUNCH"); // 하루 기록이므로 LUNCH로 통합
        
        // 전체 식사 상태를 종합하여 판단
        String overallMealStatus = determineOverallMealStatus(breakfast, lunch, dinner);
        dailyRecord.setMeal(overallMealStatus);
        Log.d("CaregiverMealRecord", "종합 식사상태: " + overallMealStatus);
        
        // 건강상태 매핑
        String mappedHealthCondition = "NORMAL"; // 기본값
        if (healthStatus.contains("좋음") || healthStatus.contains("양호")) {
            mappedHealthCondition = "GOOD";
        } else if (healthStatus.contains("나쁨") || healthStatus.contains("불량")) {
            mappedHealthCondition = "BAD";
        }
        dailyRecord.setHealthCondition(mappedHealthCondition);
        
        // 투약 여부 (medication 입력이 있으면 true)
        dailyRecord.setMedicationTaken(!medication.isEmpty());
        
        // 모든 정보를 통합하여 notes에 저장
        StringBuilder notesBuilder = new StringBuilder();
        notesBuilder.append("아침: ").append(breakfast).append("\n");
        notesBuilder.append("점심: ").append(lunch).append("\n");
        notesBuilder.append("저녁: ").append(dinner).append("\n");
        if (!medication.isEmpty()) {
            notesBuilder.append("투약: ").append(medication).append("\n");
        }
        if (!healthStatus.isEmpty()) {
            notesBuilder.append("건강상태: ").append(healthStatus).append("\n");
        }
        if (!specialNotes.isEmpty()) {
            notesBuilder.append("특이사항: ").append(specialNotes);
        }
        
        dailyRecord.setNotes(notesBuilder.toString());
        Log.d("CaregiverMealRecord", "통합 notes: " + notesBuilder.toString());
        
        // API 호출로 저장
        ApiClient.getApiService().saveDailyRecord(dailyRecord)
                .enqueue(new Callback<DailyRecord>() {
                    @Override
                    public void onResponse(Call<DailyRecord> call, Response<DailyRecord> response) {
                        if (response.isSuccessful()) {
                            // 성공 시 토스트 메시지 표시
                            if (getContext() != null) {
                                Toast.makeText(getContext(), 
                                    patientName + " 환자의 급여 내역이 저장되었습니다", 
                                    Toast.LENGTH_SHORT).show();
                            }
                            
                            // 이전 화면으로 돌아가기
                            if (getActivity() != null) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        } else {
                            // 실패 시 오류 메시지 표시
                            if (getContext() != null) {
                                Toast.makeText(getContext(), 
                                    "급여 내역 저장에 실패했습니다", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<DailyRecord> call, Throwable t) {
                        // 네트워크 오류 등
                        if (getContext() != null) {
                            Toast.makeText(getContext(), 
                                "네트워크 오류: " + t.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    
    // 전체 식사 상태를 종합하여 판단하는 메서드
    private String determineOverallMealStatus(String breakfast, String lunch, String dinner) {
        int goodCount = 0;
        int badCount = 0;
        
        // 각 식사별로 상태 확인
        if (breakfast.contains("완식") || breakfast.contains("좋음") || breakfast.contains("100")) {
            goodCount++;
        } else if (breakfast.contains("거식") || breakfast.contains("나쁨") || breakfast.contains("안먹음")) {
            badCount++;
        }
        
        if (lunch.contains("완식") || lunch.contains("좋음") || lunch.contains("100")) {
            goodCount++;
        } else if (lunch.contains("거식") || lunch.contains("나쁨") || lunch.contains("안먹음")) {
            badCount++;
        }
        
        if (dinner.contains("완식") || dinner.contains("좋음") || dinner.contains("100")) {
            goodCount++;
        } else if (dinner.contains("거식") || dinner.contains("나쁨") || dinner.contains("안먹음")) {
            badCount++;
        }
        
        // 종합 판단
        if (goodCount >= 2) {
            return "GOOD";
        } else if (badCount >= 2) {
            return "BAD";
        } else {
            return "NORMAL";
        }
    }
}