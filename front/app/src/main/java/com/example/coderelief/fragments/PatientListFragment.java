package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.adapters.PatientAdapter;
import com.example.coderelief.adapters.FamilyMemberAdapter;
import com.example.coderelief.models.Patient;
import com.example.coderelief.api.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class PatientListFragment extends Fragment implements PatientAdapter.OnPatientActionListener, FamilyMemberAdapter.OnFamilyActionListener {
    
    private TextView tvTitle;
    private RecyclerView recyclerViewPatients;
    private PatientAdapter patientAdapter;
    private FamilyMemberAdapter familyMemberAdapter;
    
    private String userRole;
    private List<Patient> patients;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_list, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getUserRole();
        setupRecyclerView();
        loadSamplePatients();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        recyclerViewPatients = view.findViewById(R.id.recycler_view_patients);
    }
    
    private void getUserRole() {
        Bundle args = getArguments();
        if (args != null) {
            userRole = args.getString("user_role", "guardian");
        } else {
            userRole = "guardian";
        }
        
        // Set title based on role
        if ("caregiver".equals(userRole)) {
            tvTitle.setText("수급자 목록");
        } else {
            tvTitle.setText("우리 가족 소식");
        }
    }
    
    private void setupRecyclerView() {
        recyclerViewPatients.setLayoutManager(new LinearLayoutManager(getContext()));
        
        if ("caregiver".equals(userRole)) {
            // 직원용 - 기존 PatientAdapter 사용
            patientAdapter = new PatientAdapter(this);
            recyclerViewPatients.setAdapter(patientAdapter);
        } else {
            // 보호자용 - FamilyMemberAdapter 사용
            familyMemberAdapter = new FamilyMemberAdapter(this);
            recyclerViewPatients.setAdapter(familyMemberAdapter);
        }
    }
    
    private void loadSamplePatients() {
        // API에서 실제 데이터 로드
        loadPatientsFromApi();
    }
    
    private void loadPatientsFromApi() {
        // TODO: 실제 구현에서는 로그인한 사용자의 정보를 사용
        // 현재는 테스트용으로 institutionId = 1 사용
        Long institutionId = 1L;
        
        Log.d("PatientListFragment", "API 호출 시작: institutionId = " + institutionId);
        
        // 현재 작동하는 엔드포인트 사용
        ApiClient.getApiService().getPatientsByInstitution(institutionId)
                .enqueue(new Callback<List<Patient>>() {
                    @Override
                    public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                        Log.d("PatientListFragment", "API 응답 받음: code = " + response.code());
                        
                        if (response.isSuccessful() && response.body() != null) {
                            patients = response.body();
                            Log.d("PatientListFragment", "환자 데이터 로드 성공: " + patients.size() + "명");
                            
                            // 각 환자 데이터 로그 출력
                            for (int i = 0; i < patients.size(); i++) {
                                Patient patient = patients.get(i);
                                Log.d("PatientListFragment", "환자 " + (i+1) + ": " + 
                                    "ID=" + patient.getPatientId() + 
                                    ", 이름=" + patient.getName() + 
                                    ", 나이=" + patient.getAge() + 
                                    ", 방호실=" + patient.getRoomNumber() + 
                                    ", 요양등급=" + patient.getCareLevel());
                            }
                            
                            updateUI();
                        } else {
                            Log.e("PatientListFragment", "API 응답 실패: " + response.code() + " - " + response.message());
                            // API 실패 시 하드코딩된 데이터로 폴백
                            loadFallbackData();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<Patient>> call, Throwable t) {
                        Log.e("PatientListFragment", "API 호출 실패", t);
                        // 네트워크 오류 시 하드코딩된 데이터로 폴백
                        loadFallbackData();
                    }
                });
    }
    
    private void loadFallbackData() {
        // API 실패 시 사용할 폴백 데이터
        patients = new ArrayList<>();
        patients.add(new Patient(1L, "김영희", 80, "101", "3"));
        patients.add(new Patient(2L, "박철수", 85, "102", "2"));
        patients.add(new Patient(3L, "이미영", 78, "103", "4"));
        updateUI();
    }
    
    private void updateUI() {
        Log.d("PatientListFragment", "UI 업데이트 시작: 환자 수 = " + (patients != null ? patients.size() : 0));
        Log.d("PatientListFragment", "사용자 역할: " + userRole);
        
        if ("caregiver".equals(userRole)) {
            Log.d("PatientListFragment", "직원용 PatientAdapter 사용");
            patientAdapter.updatePatients(patients);
        } else {
            Log.d("PatientListFragment", "보호자용 FamilyMemberAdapter 사용");
            familyMemberAdapter.updateFamilyMembers(patients);
        }
        
        Log.d("PatientListFragment", "UI 업데이트 완료");
    }
    
    // PatientAdapter.OnPatientActionListener 구현 (직원용)
    @Override
    public void onPatientDetailClick(Patient patient) {
        CaregiverPatientDetailFragment fragment = new CaregiverPatientDetailFragment();
        Bundle args = new Bundle();
        args.putLong("patient_id", patient.getPatientId());
        args.putString("patient_name", patient.getName());
        args.putInt("patient_age", patient.getAge());
        args.putString("room_number", patient.getRoomNumber());
        args.putString("care_level", patient.getCareLevel());
        args.putString("user_role", userRole);
        fragment.setArguments(args);
        
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    @Override
    public void onContactGuardianClick(Patient patient) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        
        // 현재 사용자 역할에 따라 chat_type 설정
        if ("caregiver".equals(userRole)) {
            // 요양보호사가 보호자와 채팅하는 경우
            args.putString("chat_type", "caregiver");
        } else {
            // 보호자가 요양보호사와 채팅하는 경우 (기존 로직)
            args.putString("chat_type", "guardian");
        }
        
        args.putLong("patient_id", patient.getPatientId());
        args.putString("patient_name", patient.getName());
        args.putString("user_role", userRole);
        fragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    // FamilyMemberAdapter.OnFamilyActionListener 구현 (보호자용)
    @Override
    public void onFamilyDetailClick(Patient familyMember) {
        GuardianPatientDetailFragment fragment = new GuardianPatientDetailFragment();
        Bundle args = new Bundle();
        args.putLong("patient_id", familyMember.getPatientId());
        args.putString("patient_name", familyMember.getName());
        args.putInt("patient_age", familyMember.getAge());
        args.putString("room_number", familyMember.getRoomNumber());
        args.putString("care_level", familyMember.getCareLevel());
        args.putString("user_role", userRole);
        // 요양원 이름도 전달 (실제로는 DB에서 조회)
        args.putString("care_center_name", getCareCenterName(familyMember.getName()));
        fragment.setArguments(args);
        
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    @Override
    public void onChatWithCenterClick(Patient familyMember) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("chat_type", "care_center");
        args.putLong("patient_id", familyMember.getPatientId());
        args.putString("patient_name", familyMember.getName());
        args.putString("user_role", userRole);
        args.putString("care_center_name", getCareCenterName(familyMember.getName()));
        fragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    private String getCareCenterName(String familyName) {
        // TODO: 실제로는 Patient 모델에 careCenterName 필드 추가하거나 DB 조인으로 조회
        switch (familyName) {
            case "김영희":
                return "행복한 노인요양원";
            case "박철수":
                return "사랑채 요양원";
            case "이미영":
                return "평안 실버케어";
            default:
                return "요양원";
        }
    }
    
}