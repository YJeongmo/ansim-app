package com.example.coderelief.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.models.Patient;
import com.example.coderelief.models.Activity;
import com.example.coderelief.models.DailyRecord;
import com.example.coderelief.models.Notice;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.utils.CsvExporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaregiverPatientDetailFragment extends Fragment {
    
    private TextView tvTitle, tvPatientName, tvPatientInfo;
    private LinearLayout btnMealRecord, btnActivityRecord, btnNewsWrite, btnIndividualNotice, btnAiHealthCheck, btnExportPatientInfo;
    
    private String patientName;
    private String userRole;
    private Long patientId; // patientId 필드 추가
    
    private static final int REQUEST_WRITE_STORAGE = 1001;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caregiver_patient_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        loadPatientInfo();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvPatientName = view.findViewById(R.id.tv_patient_name);
        tvPatientInfo = view.findViewById(R.id.tv_patient_info);
        btnMealRecord = view.findViewById(R.id.btn_meal_record);
        btnActivityRecord = view.findViewById(R.id.btn_activity_record);
        btnNewsWrite = view.findViewById(R.id.btn_news_write);
        btnIndividualNotice = view.findViewById(R.id.btn_individual_notice);
        btnAiHealthCheck = view.findViewById(R.id.btn_ai_health_check);
        btnExportPatientInfo = view.findViewById(R.id.btn_export_patient_info);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "환자");
            patientId = args.getLong("patient_id", 0L); // patient_id 추가
            userRole = args.getString("user_role", "caregiver");
        } else {
            patientName = "환자";
            patientId = 0L;
            userRole = "caregiver";
        }
    }
    
    private void loadPatientInfo() {
        tvTitle.setText("환자 상세 정보");
        tvPatientName.setText(patientName);
        
        // API에서 실제 환자 정보 로드
        loadPatientInfoFromApi();
    }
    
    private void loadPatientInfoFromApi() {
        // patientId가 유효한 경우에만 API 호출
        if (patientId != null && patientId > 0L) {
            ApiClient.getApiService().getPatientById(patientId)
                    .enqueue(new Callback<Patient>() {
                        @Override
                        public void onResponse(Call<Patient> call, Response<Patient> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Patient patient = response.body();
                                displayPatientInfo(patient);
                            } else {
                                // API 실패 시 하드코딩된 데이터로 폴백
                                loadFallbackData();
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<Patient> call, Throwable t) {
                            // 네트워크 오류 시 하드코딩된 데이터로 폴백
                            loadFallbackData();
                        }
                    });
        } else {
            // patientId가 없는 경우 폴백 데이터 사용
            loadFallbackData();
        }
    }
    
    private void displayPatientInfo(Patient patient) {
        String patientInfo = String.format(
            "나이: %d세\n방호실: %s\n요양등급: %s\n입소일: %s\n보호자: %s\n연락처: %s",
            patient.getAge() != null ? patient.getAge() : 0,
            patient.getRoomNumber() != null ? patient.getRoomNumber() : "-",
            patient.getCareLevel() != null ? patient.getCareLevel() : "-",
            patient.getAdmissionDate() != null ? patient.getAdmissionDate().toString() : "-",
            "보호자", // TODO: Guardian 정보 추가
            "-" // TODO: Guardian 연락처 추가
        );
        
        tvPatientInfo.setText(patientInfo);
    }
    
    private Long getPatientIdFromName(String name) {
        // TODO: 실제 구현에서는 이름으로 환자 ID를 찾는 로직 구현
        // 현재는 하드코딩된 매핑 사용
        switch (name) {
            case "김영희": return 1L;
            case "박철수": return 2L;
            case "이미영": return 3L;
            default: return null;
        }
    }
    
    private void loadFallbackData() {
        // 하드코딩된 데이터 제거 - 기본 메시지만 표시
        String patientInfo = "환자 정보를 불러올 수 없습니다.\n관리자에게 문의하세요.";
        tvPatientInfo.setText(patientInfo);
    }
    
    private void setupClickListeners() {
        btnMealRecord.setOnClickListener(v -> {
            CaregiverMealRecordFragment fragment = new CaregiverMealRecordFragment();
            Bundle args = new Bundle();
            args.putString("patient_name", patientName);
            args.putLong("patient_id", patientId); // patient_id 추가
            args.putString("user_role", userRole);
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnActivityRecord.setOnClickListener(v -> {
            CaregiverActivityRecordFragment fragment = new CaregiverActivityRecordFragment();
            Bundle args = new Bundle();
            args.putString("patient_name", patientName);
            args.putLong("patient_id", patientId); // patient_id 추가
            args.putString("user_role", userRole);
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnNewsWrite.setOnClickListener(v -> {
            NewsWriteFragment fragment = new NewsWriteFragment();
            Bundle args = new Bundle();
            args.putString("patient_name", patientName);
            args.putString("user_role", userRole);
            // 추가 환자 정보도 전달 (Bundle에서 가져온 경우)
            Bundle currentArgs = getArguments();
            if (currentArgs != null) {
                args.putLong("patient_id", currentArgs.getLong("patient_id", 0L));
            }
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnIndividualNotice.setOnClickListener(v -> {
            CaregiverIndividualNoticeFragment fragment = new CaregiverIndividualNoticeFragment();
            Bundle args = new Bundle();
            args.putString("patient_name", patientName);
            args.putString("user_role", userRole);
            args.putLong("patient_id", patientId); // patient_id 추가
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        // AI 건강 체크 버튼
        btnAiHealthCheck.setOnClickListener(v -> {
            showAiHealthCheckDialog();
        });
        
        // 환자 정보 내보내기 버튼
        btnExportPatientInfo.setOnClickListener(v -> {
            exportPatientToExcel();
        });
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
    
    
    /**
     * 환자 정보를 CSV로 내보내기
     */
    private void exportPatientToExcel() {
        Log.d("Export", "=== CSV 내보내기 버튼 클릭 ===");
        
        // 1. 로딩 메시지 표시
        Toast.makeText(getContext(), "환자 정보를 수집하는 중...", Toast.LENGTH_SHORT).show();
        Log.d("Export", "로딩 메시지 표시됨");
        
        // 2. 환자 데이터 수집 및 CSV 생성 (권한 체크 없이 진행)
        loadPatientDataForExport();
    }
    
    /**
     * 저장소 권한 확인
     */
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireContext(), 
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 저장소 권한 요청
     */
    private void requestStoragePermission() {
        Log.d("Export", "권한 요청 시작");
        
        // Android 6.0 이상에서만 런타임 권한 요청
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Log.d("Export", "Android 6.0 이상 - 런타임 권한 요청");
            ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                REQUEST_WRITE_STORAGE);
        } else {
            Log.d("Export", "Android 6.0 미만 - 권한 자동 허용");
            // Android 6.0 미만에서는 권한이 자동으로 허용됨
            exportPatientToExcel();
        }
    }
    
    /**
     * 환자 데이터 수집 및 CSV 생성
     */
    private void loadPatientDataForExport() {
        Log.d("Export", "환자 데이터 수집 시작");
        
        // 현재 환자 정보로 테스트용 데이터 생성
        Patient currentPatient = createTestPatient();
        List<Activity> activities = createTestActivities();
        List<DailyRecord> dailyRecords = createTestDailyRecords();
        List<Notice> notices = createTestNotices();
        
        Log.d("Export", "테스트 데이터 생성 완료");
        Log.d("Export", "환자: " + currentPatient.getName());
        Log.d("Export", "활동: " + activities.size() + "개");
        Log.d("Export", "일일기록: " + dailyRecords.size() + "개");
        Log.d("Export", "공지사항: " + notices.size() + "개");
        
        try {
            Log.d("Export", "CSV 파일 생성 시작");
            
            // CSV 파일 생성
            File csvFile = CsvExporter.exportPatientInfo(
                currentPatient, activities, dailyRecords, notices, getContext()
            );
            
            Log.d("Export", "CSV 파일 생성 성공: " + csvFile.getAbsolutePath());
            
            // 성공 메시지 및 파일 공유
            showExportSuccess(csvFile);
            
        } catch (Exception e) {
            Log.e("Export", "CSV 파일 생성 실패: " + e.getMessage());
            Log.e("Export", "오류 상세: " + e.toString());
            Toast.makeText(getContext(), "CSV 파일 생성에 실패했습니다: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 테스트용 환자 데이터 생성
     */
    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setPatientId(patientId);
        patient.setName(patientName);
        patient.setAge(79);
        patient.setBirthdate("1945-08-15");
        patient.setRoomNumber("101호");
        patient.setCareLevel("1등급");
        patient.setAdmissionDate("2024-01-22");
        return patient;
    }
    
    /**
     * 테스트용 활동 데이터 생성
     */
    private List<Activity> createTestActivities() {
        List<Activity> activities = new ArrayList<>();
        
        // 활동 1: 물리치료
        Activity activity1 = new Activity();
        activity1.setActivityId(1L);
        activity1.setPatientId(patientId);
        activity1.setType("물리치료");
        activity1.setDescription("상체 근력 강화 운동");
        activity1.setNotes("09:00-10:00 완료");
        activity1.setActivityTime(new Date());
        activity1.setCreatedAt(new Date());
        activities.add(activity1);
        
        // 활동 2: 인지훈련
        Activity activity2 = new Activity();
        activity2.setActivityId(2L);
        activity2.setPatientId(patientId);
        activity2.setType("인지훈련");
        activity2.setDescription("기억력 향상 훈련");
        activity2.setNotes("14:00-15:00 완료");
        activity2.setActivityTime(new Date());
        activity2.setCreatedAt(new Date());
        activities.add(activity2);
        
        return activities;
    }
    
    /**
     * 테스트용 일일기록 데이터 생성
     */
    private List<DailyRecord> createTestDailyRecords() {
        List<DailyRecord> dailyRecords = new ArrayList<>();
        
        // 일일기록 1
        DailyRecord record1 = new DailyRecord();
        record1.setRecordId(1L);
        record1.setPatientId(patientId);
        record1.setRecordDate("2024-01-22");
        record1.setTimeSlot("BREAKFAST");
        record1.setMeal("GOOD");
        record1.setHealthCondition("GOOD");
        record1.setMedicationTaken(true);
        record1.setNotes("전반적으로 양호한 상태");
        record1.setCreatedAt("2024-01-22 09:00:00");
        dailyRecords.add(record1);
        
        return dailyRecords;
    }
    
    /**
     * 테스트용 공지사항 데이터 생성
     */
    private List<Notice> createTestNotices() {
        List<Notice> notices = new ArrayList<>();
        
        // 공지사항 1
        Notice notice1 = new Notice();
        notice1.setNoticeId(1L);
        notice1.setTitle("월간 건강검진 안내");
        notice1.setContent("다음주 월요일 오전 9시에 월간 건강검진을 실시합니다.");
        notice1.setPriority("일반");
        notice1.setCreatedAt(java.time.LocalDateTime.now());
        notices.add(notice1);
        
        return notices;
    }
    
    /**
     * 내보내기 성공 시 파일 공유 다이얼로그 표시
     */
    private void showExportSuccess(File csvFile) {
        Log.d("Export", "내보내기 성공 다이얼로그 표시");
        
        try {
            // FileProvider를 사용하여 파일 URI 생성
            Uri fileUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.coderelief.fileprovider",
                csvFile
            );
            
            // 공유 인텐트 생성
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            // 파일명 추가
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "환자정보_" + patientName + "_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date()) + ".csv");
            
            // 공유 다이얼로그 표시
            startActivity(Intent.createChooser(shareIntent, "환자 정보 공유하기"));
            
            Toast.makeText(getContext(), "CSV 파일이 생성되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d("Export", "파일 공유 다이얼로그 표시 완료");
            
        } catch (Exception e) {
            Log.e("Export", "파일 공유 실패: " + e.getMessage());
            Toast.makeText(getContext(), "파일 공유에 실패했습니다: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되면 엑셀 내보내기 재시도
                exportPatientToExcel();
            } else {
                Toast.makeText(getContext(), "파일 저장 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}