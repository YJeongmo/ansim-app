package com.example.coderelief.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.R;
import com.example.coderelief.DashboardActivity;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ConsultationRequestApiService;
import com.example.coderelief.api.InstitutionApiService;
import com.example.coderelief.models.ConsultationRequest;
import com.example.coderelief.models.Institution;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ConsultationFragment extends Fragment {
    
    private MaterialToolbar toolbar;
    private EditText etInstitutionName, etApplicantName, etApplicantPhone, etConsultationPurpose, etConsultationContent;
    private TextView tvInputPreview;
    private Button btnSubmit;
    
    private ConsultationRequestApiService consultationApiService;
    private InstitutionApiService institutionApiService;
    private String previousFragmentTag; // 이전 Fragment 태그 저장
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consultation, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        // 날짜/시간 기본값 설정 제거됨
        
        // 요양원 이름 전달받기
        loadInstitutionNameFromArguments();
        
        // 이전 Fragment 정보 받기
        loadPreviousFragmentInfo();
        
        consultationApiService = ApiClient.getConsultationRequestApiService();
        institutionApiService = ApiClient.getInstitutionApiService();
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        etInstitutionName = view.findViewById(R.id.etInstitutionName);
        etApplicantName = view.findViewById(R.id.etApplicantName);
        etApplicantPhone = view.findViewById(R.id.etApplicantPhone);
        etConsultationPurpose = view.findViewById(R.id.etConsultationPurpose);
        etConsultationContent = view.findViewById(R.id.etConsultationContent);
        tvInputPreview = view.findViewById(R.id.tvInputPreview);
        btnSubmit = view.findViewById(R.id.btnSubmit);
    }
    
    private void setupClickListeners() {
        // 뒤로가기 버튼 클릭 이벤트
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).onBackPressed();
            }
        });
        
        btnSubmit.setOnClickListener(v -> submitConsultationRequest());
        
        // 입력 필드 텍스트 변경 리스너 추가
        etInstitutionName.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) { updateInputPreview(); }
        });
        
        etApplicantName.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) { updateInputPreview(); }
        });
        
        etApplicantPhone.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) { updateInputPreview(); }
        });
        
        etConsultationPurpose.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) { updateInputPreview(); }
        });
        
        etConsultationContent.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) { updateInputPreview(); }
        });
    }
    
    // 날짜/시간 기본값 설정 메서드 제거됨
    
    private void loadInstitutionNameFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            String institutionName = args.getString("institution_name", "");
            String careCenterName = args.getString("care_center_name", "");
            String name = args.getString("name", "");
            
            // 우선순위: institution_name > care_center_name > name
            String finalInstitutionName = "";
            if (!institutionName.isEmpty()) {
                finalInstitutionName = institutionName;
            } else if (!careCenterName.isEmpty()) {
                finalInstitutionName = careCenterName;
            } else if (!name.isEmpty()) {
                finalInstitutionName = name;
            }
            
            if (!finalInstitutionName.isEmpty()) {
                Log.d("ConsultationFragment", "요양원 이름 설정: " + finalInstitutionName);
                etInstitutionName.setText(finalInstitutionName);
                etInstitutionName.setEnabled(false); // 읽기 전용으로 설정
            } else {
                Log.d("ConsultationFragment", "요양원 이름이 전달되지 않음, 사용자 입력 허용");
            }
        }
    }
    
    private void loadPreviousFragmentInfo() {
        Bundle args = getArguments();
        if (args != null) {
            previousFragmentTag = args.getString("previous_fragment_tag", "");
            Log.d("ConsultationFragment", "이전 Fragment 태그: " + previousFragmentTag);
        }
    }
    
    // 날짜/시간 선택 기능 제거됨
    
    private void updateInputPreview() {
        StringBuilder preview = new StringBuilder();
        preview.append("요양원명: ").append(etInstitutionName.getText().toString().trim()).append("\n");
        preview.append("신청자명: ").append(etApplicantName.getText().toString().trim()).append("\n");
        preview.append("연락처: ").append(etApplicantPhone.getText().toString().trim()).append("\n");
        preview.append("상담 목적: ").append(etConsultationPurpose.getText().toString().trim()).append("\n");
        preview.append("상담 내용: ").append(etConsultationContent.getText().toString().trim());
        
        tvInputPreview.setText(preview.toString());
    }
    
    private void submitConsultationRequest() {
        Log.d("ConsultationForm", "=== 상담신청 제출 시작 ===");
        
        // 입력값 검증
        String institutionName = etInstitutionName.getText().toString().trim();
        String applicantName = etApplicantName.getText().toString().trim();
        String applicantPhone = etApplicantPhone.getText().toString().trim();
        String consultationPurpose = etConsultationPurpose.getText().toString().trim();
        String consultationContent = etConsultationContent.getText().toString().trim();
        
        Log.d("ConsultationForm", "입력값 검증 시작:");
        Log.d("ConsultationForm", "- 요양원명: '" + institutionName + "'");
        Log.d("ConsultationForm", "- 신청자명: '" + applicantName + "'");
        Log.d("ConsultationForm", "- 연락처: '" + applicantPhone + "'");
        Log.d("ConsultationForm", "- 상담 목적: '" + consultationPurpose + "'");
        Log.d("ConsultationForm", "- 상담 내용: '" + consultationContent + "'");
        // 날짜/시간 선택 기능 제거됨
        
        if (institutionName.isEmpty()) {
            Log.e("ConsultationForm", "❌ 요양원명 누락");
            Toast.makeText(getContext(), "요양원명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (applicantName.isEmpty()) {
            Log.e("ConsultationForm", "❌ 신청자명 누락");
            Toast.makeText(getContext(), "신청자명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (applicantPhone.isEmpty()) {
            Log.e("ConsultationForm", "❌ 연락처 누락");
            Toast.makeText(getContext(), "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (consultationPurpose.isEmpty()) {
            Log.e("ConsultationForm", "❌ 상담 목적 누락");
            Toast.makeText(getContext(), "상담 목적을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (consultationContent.isEmpty()) {
            Log.e("ConsultationForm", "❌ 상담 내용 누락");
            Toast.makeText(getContext(), "상담 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 날짜/시간 검증 제거됨
        
        Log.d("ConsultationForm", "✅ 모든 입력값 검증 통과");
        
        // ConsultationRequest 객체 생성
        ConsultationRequest request = new ConsultationRequest();
        request.setInstitutionName(institutionName);
        request.setApplicantName(applicantName);
        request.setApplicantPhone(applicantPhone);
        request.setConsultationPurpose(consultationPurpose);
        request.setConsultationContent(consultationContent);
        // 날짜/시간 필드 제거됨
        
        // 상세 로깅
        Log.d("ConsultationForm", "=== 상담신청 제출 정보 ===");
        Log.d("ConsultationForm", "요양원명: " + institutionName);
        Log.d("ConsultationForm", "신청자명: " + applicantName);
        Log.d("ConsultationForm", "연락처: " + applicantPhone);
        Log.d("ConsultationForm", "상담 목적: " + consultationPurpose);
        Log.d("ConsultationForm", "상담 내용: " + consultationContent);
        Log.d("ConsultationForm", "전체 객체: " + request.toString());
        
        // 요양원 정보 조회 후 상담 신청
        lookupInstitutionAndSubmit(institutionName, request);
    }
    
    private void lookupInstitutionAndSubmit(String institutionName, ConsultationRequest request) {
        Log.d("ConsultationForm", "=== 요양원 정보 조회 시작 ===");
        Log.d("ConsultationForm", "조회할 요양원명: " + institutionName);
        
        // 요양원명으로 기관 조회
        Call<List<Institution>> call = institutionApiService.getInstitutionsByName(institutionName);
        call.enqueue(new Callback<List<Institution>>() {
            @Override
            public void onResponse(Call<List<Institution>> call, Response<List<Institution>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Institution institution = response.body().get(0);
                    Log.d("ConsultationForm", "✅ 요양원 정보 조회 성공: " + institution.getInstitutionName());
                    Log.d("ConsultationForm", "요양원 ID: " + institution.getInstitutionId());
                    
                    // 요양원 ID를 request에 설정
                    request.setInstitutionId(institution.getInstitutionId());
                    
                    // 상담 신청 제출
                    submitConsultationRequest(request);
                } else {
                    Log.w("ConsultationForm", "⚠️ 요양원 정보를 찾을 수 없음: " + institutionName);
                    Toast.makeText(getContext(), "요양원 정보를 찾을 수 없습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<Institution>> call, Throwable t) {
                Log.e("ConsultationForm", "❌ 요양원 정보 조회 실패", t);
                Toast.makeText(getContext(), "요양원 정보 조회에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void submitConsultationRequest(ConsultationRequest request) {
        Log.d("ConsultationForm", "=== 상담 신청 API 호출 시작 ===");
        
        Call<ConsultationRequest> call = consultationApiService.createConsultationRequest(request);
        call.enqueue(new Callback<ConsultationRequest>() {
            @Override
            public void onResponse(Call<ConsultationRequest> call, Response<ConsultationRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ConsultationForm", "✅ 상담신청 성공!");
                    Toast.makeText(getContext(), "상담 신청이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    navigateBackToPreviousFragment();
                } else {
                    Log.e("ConsultationForm", "❌ 상담신청 실패: " + response.code());
                    Toast.makeText(getContext(), "상담 신청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ConsultationRequest> call, Throwable t) {
                Log.e("ConsultationForm", "❌ 상담신청 API 호출 실패", t);
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void clearForm() {
        etInstitutionName.setText("");
        etApplicantName.setText("");
        etApplicantPhone.setText("");
        etConsultationPurpose.setText("");
        etConsultationContent.setText("");
        // 날짜/시간 설정 제거됨
    }
    
    private void navigateBackToPreviousFragment() {
        if (getActivity() != null) {
            // 스케줄 페이지를 찾아서 새로고침 호출
            refreshScheduleFragmentIfExists();
            
            // 이전 Fragment로 돌아가기
            if (getActivity() instanceof DashboardActivity) {
                DashboardActivity activity = (DashboardActivity) getActivity();
                
                // 이전 Fragment가 요양원 상세정보인 경우
                if ("CareCenterInfoFragment".equals(previousFragmentTag)) {
                    Log.d("ConsultationFragment", "요양원 상세정보 페이지로 돌아가기");
                    // 백스택에서 이전 Fragment로 이동
                    getParentFragmentManager().popBackStack();
                } else {
                    Log.d("ConsultationFragment", "기본 뒤로가기");
                    // 일반적인 뒤로가기
                    getParentFragmentManager().popBackStack();
                }
            }
        }
    }
    
    /**
     * ScheduleFragment를 찾아서 새로고침하는 메서드
     */
    private void refreshScheduleFragmentIfExists() {
        try {
            // Fragment Manager를 통해 현재 활성화된 모든 Fragment를 확인
            for (Fragment fragment : getParentFragmentManager().getFragments()) {
                if (fragment instanceof ScheduleFragment) {
                    Log.d("ConsultationFragment", "ScheduleFragment 발견 - 새로고침 호출");
                    ((ScheduleFragment) fragment).refreshSchedule();
                    return;
                }
            }
            Log.d("ConsultationFragment", "ScheduleFragment를 찾을 수 없음");
        } catch (Exception e) {
            Log.e("ConsultationFragment", "ScheduleFragment 새로고침 실패: " + e.getMessage());
        }
    }
}
