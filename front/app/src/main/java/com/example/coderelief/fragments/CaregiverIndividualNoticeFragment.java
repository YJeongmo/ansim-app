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
import com.google.android.material.textfield.TextInputEditText;

public class CaregiverIndividualNoticeFragment extends Fragment {
    
    private TextView tvTitle, tvPatientName;
    private Button btnPriorityUrgent, btnPriorityImportant, btnPriorityNormal;
    private TextInputEditText etNoticeTitle, etNoticeContent;
    private Button btnSend;
    
    private String patientName;
    private String selectedPriority = "일반"; // Default priority
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caregiver_individual_notice, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        setupClickListeners();
        updatePriorityButtons();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvPatientName = view.findViewById(R.id.tv_patient_name);
        btnPriorityUrgent = view.findViewById(R.id.btn_priority_urgent);
        btnPriorityImportant = view.findViewById(R.id.btn_priority_important);
        btnPriorityNormal = view.findViewById(R.id.btn_priority_normal);
        etNoticeTitle = view.findViewById(R.id.et_notice_title);
        etNoticeContent = view.findViewById(R.id.et_notice_content);
        btnSend = view.findViewById(R.id.btn_send);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "환자");
        } else {
            patientName = "환자";
        }
        
        tvTitle.setText("개별 공지 작성");
        tvPatientName.setText(patientName + " 환자 개별 공지");
    }
    
    private void setupClickListeners() {
        btnPriorityUrgent.setOnClickListener(v -> {
            selectedPriority = "긴급";
            updatePriorityButtons();
        });
        
        btnPriorityImportant.setOnClickListener(v -> {
            selectedPriority = "중요";
            updatePriorityButtons();
        });
        
        btnPriorityNormal.setOnClickListener(v -> {
            selectedPriority = "일반";
            updatePriorityButtons();
        });
        
        btnSend.setOnClickListener(v -> {
            String noticeTitle = etNoticeTitle.getText() != null ? etNoticeTitle.getText().toString().trim() : "";
            String noticeContent = etNoticeContent.getText() != null ? etNoticeContent.getText().toString().trim() : "";
            
            // Validate required fields
            if (noticeTitle.isEmpty() || noticeContent.isEmpty()) {
                Toast.makeText(getContext(), "공지 제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Send individual notice (TODO: implement actual sending)
            sendIndividualNotice(selectedPriority, noticeTitle, noticeContent);
        });
    }
    
    private void updatePriorityButtons() {
        // Reset all buttons to default color
        btnPriorityUrgent.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnPriorityImportant.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnPriorityNormal.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        
        // Highlight selected button
        if ("긴급".equals(selectedPriority)) {
            btnPriorityUrgent.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if ("중요".equals(selectedPriority)) {
            btnPriorityImportant.setBackgroundColor(getResources().getColor(R.color.purple_500));
        } else {
            btnPriorityNormal.setBackgroundColor(getResources().getColor(R.color.purple_500));
        }
    }
    
    private void sendIndividualNotice(String priority, String noticeTitle, String noticeContent) {
        // TODO: Implement actual send functionality (database/API call)
        
        // For now, show success message and go back
        Toast.makeText(getContext(), 
            patientName + " 환자 보호자에게 " + priority + " 공지가 전송되었습니다", 
            Toast.LENGTH_SHORT).show();
        
        // Go back to previous screen
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}