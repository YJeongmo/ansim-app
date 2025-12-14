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
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ConsultationRequestApiService;
import com.example.coderelief.models.ConsultationRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationRequestDetailFragment extends Fragment {
    
    private static final String ARG_CONSULTATION_REQUEST = "consultation_request";
    
    private ConsultationRequest consultationRequest;
    private ConsultationRequestApiService apiService;
    
    private TextView tvApplicantName, tvApplicantPhone, tvCreatedAt;
    private TextView tvConsultationPurpose, tvConsultationContent;
    private Button btnBack, btnDelete;
    
    public static ConsultationRequestDetailFragment newInstance(ConsultationRequest request) {
        ConsultationRequestDetailFragment fragment = new ConsultationRequestDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONSULTATION_REQUEST, request);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            consultationRequest = (ConsultationRequest) getArguments().getSerializable(ARG_CONSULTATION_REQUEST);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consultation_request_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        populateData();
        
        apiService = ApiClient.getConsultationRequestApiService();
    }
    
    private void initViews(View view) {
        tvApplicantName = view.findViewById(R.id.tvApplicantName);
        tvApplicantPhone = view.findViewById(R.id.tvApplicantPhone);
        tvCreatedAt = view.findViewById(R.id.tvCreatedAt);
        tvConsultationPurpose = view.findViewById(R.id.tvConsultationPurpose);
        tvConsultationContent = view.findViewById(R.id.tvConsultationContent);
        btnBack = view.findViewById(R.id.btnBack);
        btnDelete = view.findViewById(R.id.btnDelete);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
    }
    
    private void populateData() {
        if (consultationRequest != null) {
            tvApplicantName.setText(consultationRequest.getApplicantName());
            tvApplicantPhone.setText(consultationRequest.getApplicantPhone());
            tvCreatedAt.setText(consultationRequest.getCreatedAtString());
            tvConsultationPurpose.setText(consultationRequest.getConsultationPurpose());
            tvConsultationContent.setText(consultationRequest.getConsultationContent());
        }
    }
    
    private void showDeleteConfirmDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("상담 신청 삭제")
                .setMessage("정말로 이 상담 신청을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> deleteConsultationRequest())
                .setNegativeButton("취소", null);
        
        builder.create().show();
    }
    
    private void deleteConsultationRequest() {
        if (consultationRequest == null) return;
        
        Call<Object> call = apiService.deleteConsultationRequest(consultationRequest.getRequestId());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "상담 신청이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                } else {
                    Toast.makeText(getContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
