package com.example.coderelief.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationApprovalDialog extends DialogFragment {
    
    private TextView tvReservationInfo;
    private RadioGroup rgApprovalStatus;
    private TextInputEditText etApprovalReason;
    private Button btnCancelApproval, btnProcessReservation;
    
    private Map<String, Object> reservationData;
    private OnReservationProcessedListener onReservationProcessedListener;
    
    public interface OnReservationProcessedListener {
        void onReservationProcessed();
    }
    
    public void setOnReservationProcessedListener(OnReservationProcessedListener listener) {
        this.onReservationProcessedListener = listener;
    }
    
    public static ReservationApprovalDialog newInstance(Map<String, Object> reservation) {
        ReservationApprovalDialog dialog = new ReservationApprovalDialog();
        Bundle args = new Bundle();
        // Convert reservation data to bundle
        for (Map.Entry<String, Object> entry : reservation.entrySet()) {
            if (entry.getValue() instanceof String) {
                args.putString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                args.putLong(entry.getKey(), (Long) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                args.putInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Double) {
                args.putDouble(entry.getKey(), (Double) entry.getValue());
            }
        }
        dialog.setArguments(args);
        return dialog;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_reservation_approval, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        loadReservationData();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tvReservationInfo = view.findViewById(R.id.tv_reservation_info);
        rgApprovalStatus = view.findViewById(R.id.rg_approval_status);
        etApprovalReason = view.findViewById(R.id.et_approval_reason);
        btnCancelApproval = view.findViewById(R.id.btn_cancel_approval);
        btnProcessReservation = view.findViewById(R.id.btn_process_reservation);
    }
    
    private void loadReservationData() {
        Bundle args = getArguments();
        if (args != null) {
            reservationData = new HashMap<>();
            for (String key : args.keySet()) {
                reservationData.put(key, args.get(key));
            }
            
            // Display reservation information
            String patientName = args.getString("patientName", "환자명 없음");
            String appointmentType = getAppointmentTypeKorean(args.getString("appointmentType", ""));
            
            String startTime = args.getString("startTime", "");
            String endTime = args.getString("endTime", "");
            String dateTime = formatDateTime(startTime, endTime);
            
            String reason = args.getString("reason", "목적 정보 없음");
            
            String relationship = args.getString("visitorRelationship", "");
            int visitorCount = args.getInt("visitorCount", 1);
            String visitorInfo = String.format("%s (%d명)", relationship, visitorCount);
            
            String reservationInfo = String.format(
                "환자명: %s\n예약 유형: %s\n일시: %s\n목적: %s\n방문자: %s",
                patientName, appointmentType, dateTime, reason, visitorInfo
            );
            tvReservationInfo.setText(reservationInfo);
        }
    }
    
    private void setupClickListeners() {
        btnProcessReservation.setOnClickListener(v -> {
            int selectedId = rgApprovalStatus.getCheckedRadioButtonId();
            String action = (selectedId == R.id.rb_approve) ? "APPROVED" : "REJECTED";
            processReservation(action);
        });
        btnCancelApproval.setOnClickListener(v -> dismiss());
    }
    
    private void processReservation(String action) {
        if (reservationData == null) return;
        
        Object appointmentIdObj = reservationData.get("appointmentId");
        if (appointmentIdObj == null) {
            Toast.makeText(getContext(), "예약 ID를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Long appointmentId;
        if (appointmentIdObj instanceof Number) {
            appointmentId = ((Number) appointmentIdObj).longValue();
        } else {
            Toast.makeText(getContext(), "예약 ID 형식 오류", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Disable buttons during processing
        btnProcessReservation.setEnabled(false);
        btnProcessReservation.setText("처리 중...");
        
        Map<String, Object> approvalData = new HashMap<>();
        approvalData.put("approvalStatus", action);
        approvalData.put("staffNotes", etApprovalReason.getText() != null ? etApprovalReason.getText().toString().trim() : "");
        
        ApiClient.getReservationApiService()
                .processApproval(appointmentId, approvalData)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        btnProcessReservation.setEnabled(true);
                        btnProcessReservation.setText("처리 완료");
                        
                        if (response.isSuccessful()) {
                            String message = "APPROVED".equals(action) ? "예약이 승인되었습니다" : "예약이 거부되었습니다";
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            
                            if (onReservationProcessedListener != null) {
                                onReservationProcessedListener.onReservationProcessed();
                            }
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "처리에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        btnProcessReservation.setEnabled(true);
                        btnProcessReservation.setText("처리 완료");
                        Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private String getAppointmentTypeKorean(String appointmentType) {
        switch (appointmentType) {
            case "VISIT": return "상담";
            case "OUTING": return "외출";
            case "OVERNIGHT": return "외박";
            case "CONSULTATION": return "상담";
            default: return appointmentType;
        }
    }
    
    private String formatDateTime(String startTime, String endTime) {
        if (startTime == null || startTime.isEmpty()) return "시간 정보 없음";
        
        try {
            // Extract date and time from ISO format
            String[] startParts = startTime.split("T");
            String date = startParts[0];
            String startTimeOnly = startParts[1].substring(0, 5);
            
            String endTimeOnly = "";
            if (endTime != null && !endTime.isEmpty()) {
                String[] endParts = endTime.split("T");
                endTimeOnly = endParts[1].substring(0, 5);
            }
            
            return String.format("%s %s - %s", date, startTimeOnly, endTimeOnly);
        } catch (Exception e) {
            return startTime;
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}