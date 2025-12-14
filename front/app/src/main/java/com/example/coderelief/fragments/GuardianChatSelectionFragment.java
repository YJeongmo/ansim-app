package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.adapters.FamilyMemberAdapter;
import com.example.coderelief.models.Patient;
import com.example.coderelief.api.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuardianChatSelectionFragment extends Fragment implements FamilyMemberAdapter.OnFamilyActionListener {
    private RecyclerView recyclerFamilyChat;
    private FamilyMemberAdapter familyMemberAdapter;
    private List<Patient> familyMembers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardian_chat_selection, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadFamilyMembers();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerFamilyChat = view.findViewById(R.id.rv_family_chat_list);
    }
    
    private void setupRecyclerView() {
        familyMemberAdapter = new FamilyMemberAdapter(this);
        recyclerFamilyChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFamilyChat.setAdapter(familyMemberAdapter);
    }
    
    private void loadFamilyMembers() {
        // API에서 실제 데이터 로드
        loadFamilyMembersFromApi();
    }
    
    private void loadFamilyMembersFromApi() {
        // TODO: 실제 구현에서는 로그인한 보호자의 정보를 사용
        // 현재는 테스트용으로 guardianId = 1 사용
        Long guardianId = 1L;
        
        ApiClient.getApiService().getPatientByGuardian(guardianId)
                .enqueue(new Callback<Patient>() {
                    @Override
                    public void onResponse(Call<Patient> call, Response<Patient> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Patient patient = response.body();
                            familyMembers = new ArrayList<>();
                            familyMembers.add(patient);
                            familyMemberAdapter.updateFamilyMembers(familyMembers);
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
    }
    
    private void loadFallbackData() {
        // 하드코딩된 데이터 제거 - API 실패 시 빈 목록 표시
        familyMembers = new ArrayList<>();
        familyMemberAdapter.updateFamilyMembers(familyMembers);
        
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "가족 정보를 불러올 수 없습니다.", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFamilyDetailClick(Patient familyMember) {
        // 채팅 선택 화면에서는 상세보기 대신 채팅으로 바로 이동
        onChatWithCenterClick(familyMember);
    }

    @Override
    public void onChatWithCenterClick(Patient familyMember) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("chat_type", "care_center");
        args.putLong("patient_id", familyMember.getPatientId());
        args.putString("patient_name", familyMember.getName());
        args.putString("user_role", "guardian");
        args.putString("care_center_name", getCareCenterName(familyMember.getName()));
        fragment.setArguments(args);
        
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    private String getCareCenterName(String familyName) {
        // PatientListFragment와 동일한 로직 사용
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