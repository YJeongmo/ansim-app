package com.example.coderelief.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.adapters.CalendarAdapter;
import com.example.coderelief.dialogs.ReservationRequestDialog;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ApiService;
import com.example.coderelief.api.ReservationApiService;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ScheduleFragment extends Fragment {
    
    private TextView tvTitle, tvCurrentMonth, tvSelectedDate;
    private ImageButton btnPrevMonth, btnNextMonth;
    private GridView gvCalendar;
    private RecyclerView rvDailySchedule;
    private LinearLayout layoutEmptySchedule;
    private Button btnAddSchedule;
    private Button btnQuickTherapy, btnQuickActivity;
    
    private String userRole;
    private boolean managementMode;
    private Calendar currentCalendar;
    private Calendar selectedDate;
    private SimpleDateFormat monthFormat;
    private SimpleDateFormat dateFormat;
    private CalendarAdapter calendarAdapter;
    private List<Map<String, Object>> reservationList;
    private Long highlightReservationId; // 하이라이트할 예약 ID
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initCalendar();
        getArgumentsData();
        setupRoleBasedVisibility();
        setupClickListeners();
        setupRecyclerView();
        setupCalendar();
        
        // TODO: Load schedule data from database/API
        loadScheduleData();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvCurrentMonth = view.findViewById(R.id.tv_current_month);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
        btnPrevMonth = view.findViewById(R.id.btn_prev_month);
        btnNextMonth = view.findViewById(R.id.btn_next_month);
        gvCalendar = view.findViewById(R.id.gv_calendar);
        rvDailySchedule = view.findViewById(R.id.rv_daily_schedule);
        layoutEmptySchedule = view.findViewById(R.id.layout_empty_schedule);
        btnAddSchedule = view.findViewById(R.id.btn_add_schedule);
        btnQuickTherapy = view.findViewById(R.id.btn_quick_therapy);
        btnQuickActivity = view.findViewById(R.id.btn_quick_activity);
    }
    
    private void initCalendar() {
        currentCalendar = Calendar.getInstance();
        selectedDate = Calendar.getInstance();
        monthFormat = new SimpleDateFormat("yyyy년 M월", Locale.KOREAN);
        dateFormat = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN);
        
        updateMonthDisplay();
        updateSelectedDateDisplay();
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            userRole = args.getString("user_role", "guardian");
            managementMode = args.getBoolean("management_mode", false);
            highlightReservationId = args.getLong("highlight_reservation_id", -1L);
            if (highlightReservationId == -1L) {
                highlightReservationId = null;
            }
        } else {
            userRole = "guardian";
            managementMode = false;
            highlightReservationId = null;
        }
    }
    
    private void setupRoleBasedVisibility() {
        if ("caregiver".equals(userRole)) {
            if (managementMode) {
                tvTitle.setText("일정 관리");
                btnAddSchedule.setVisibility(View.VISIBLE);
                // Show all quick schedule buttons
                btnQuickTherapy.setVisibility(View.VISIBLE);
                btnQuickActivity.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setText("상담 일정 확인");
                btnAddSchedule.setVisibility(View.VISIBLE);
                // Show limited quick schedule buttons
                btnQuickTherapy.setVisibility(View.GONE);
                btnQuickActivity.setVisibility(View.GONE);
            }
        } else {
            // Guardian view - read only
            tvTitle.setText("일정 확인/예약");
            btnAddSchedule.setVisibility(View.GONE);
            // Show only relevant buttons for guardians
            btnQuickTherapy.setVisibility(View.GONE);
            btnQuickActivity.setVisibility(View.GONE);
        }
    }
    
    private void setupClickListeners() {
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateMonthDisplay();
            updateCalendar();
            loadScheduleData();
        });
        
        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateMonthDisplay();
            updateCalendar();
            loadScheduleData();
        });
        
        btnAddSchedule.setOnClickListener(v -> {
            // TODO: Show add schedule dialog or navigate to add schedule fragment
            showAddScheduleDialog();
        });
        
        // Quick schedule buttons
        btnQuickTherapy.setOnClickListener(v -> createQuickSchedule("물리치료"));
        btnQuickActivity.setOnClickListener(v -> createQuickSchedule("활동프로그램"));
    }
    
    private void setupRecyclerView() {
        rvDailySchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Set adapter with daily schedule data
        // DailyScheduleAdapter adapter = new DailyScheduleAdapter(scheduleList, this::onScheduleClick);
        // rvDailySchedule.setAdapter(adapter);
    }
    
    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(getContext(), currentCalendar);
        gvCalendar.setAdapter(calendarAdapter);
        
        gvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDateSelected(position);
            }
        });
    }
    
    private void updateMonthDisplay() {
        tvCurrentMonth.setText(monthFormat.format(currentCalendar.getTime()));
    }
    
    private void updateSelectedDateDisplay() {
        tvSelectedDate.setText("선택된 날짜: " + dateFormat.format(selectedDate.getTime()));
    }
    
    private void updateCalendar() {
        if (calendarAdapter != null) {
            calendarAdapter.updateMonth(currentCalendar);
        }
    }
    
    private void onDateSelected(int position) {
        if (calendarAdapter != null) {
            calendarAdapter.setSelectedPosition(position);
            CalendarAdapter.CalendarDay selectedDay = calendarAdapter.getSelectedDay();
            
            if (selectedDay != null && selectedDay.dayNumber > 0 && selectedDay.isCurrentMonth) {
                selectedDate.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                selectedDate.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
                selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay.dayNumber);
                
                updateSelectedDateDisplay();
                loadDailySchedule();
            }
        }
    }
    
    private void loadScheduleData() {
        Bundle args = getArguments();
        if (args == null) return;
        
        if ("guardian".equals(userRole)) {
            Long guardianId = args.getLong("guardian_id", 0L);
            if (guardianId > 0L) {
                loadPatientByGuardian(guardianId);
            }
        } else {
            // For caregiver, load pending reservations
            loadPendingReservations();
        }
        
        // 하이라이트할 예약이 있으면 해당 예약의 날짜로 이동
        if (highlightReservationId != null) {
            highlightReservation();
        }
    }
    
    private void loadDailySchedule() {
        if ("caregiver".equals(userRole)) {
            // Filter reservations for selected date for both management and consultation mode
            filterReservationsForSelectedDate();
        } else if ("guardian".equals(userRole)) {
            // Filter reservations for selected date
            filterReservationsForSelectedDate();
        } else {
            // Show scheduled items for normal view
            showEmptySchedule(true);
        }
    }
    
    private void loadPatientByGuardian(Long guardianId) {
        ApiService apiService = ApiClient.getApiService();
        Call<com.example.coderelief.models.Patient> call = apiService.getPatientByGuardian(guardianId);
        
        call.enqueue(new Callback<com.example.coderelief.models.Patient>() {
            @Override
            public void onResponse(Call<com.example.coderelief.models.Patient> call, Response<com.example.coderelief.models.Patient> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.coderelief.models.Patient patient = response.body();
                    Long patientId = patient.getPatientId();
                    loadPatientReservations(patientId);
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "환자 정보 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<com.example.coderelief.models.Patient> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void loadPatientReservations(Long patientId) {
        ReservationApiService reservationApiService = ApiClient.getReservationApiService();
        Call<List<Map<String, Object>>> call = reservationApiService.getReservationsByPatient(patientId);
        
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reservationList = response.body();
                    updateScheduleDisplay();
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "예약 데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void loadGuardianReservations(Long guardianId) {
        // 보호자의 환자 정보를 먼저 가져온 후, 해당 환자의 예약을 로드
        loadPatientByGuardian(guardianId);
    }
    
    private void loadPendingReservations() {
        // Load all reservations for caregiver view, not just pending ones
        loadAllReservations();
    }
    
    private void loadAllReservations() {
        ReservationApiService reservationApiService = ApiClient.getReservationApiService();
        // Use getReservationsByInstitution with null parameters to get all reservations
        // Assuming institutionId = 1 for now (this should be dynamic based on caregiver's institution)
        Call<List<Map<String, Object>>> call = reservationApiService.getReservationsByInstitution(1L, null, null);
        
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reservationList = response.body();
                    
                    // Sort reservations: PENDING first, then processed ones by date
                    Collections.sort(reservationList, (a, b) -> {
                        String statusA = (String) a.get("status");
                        String statusB = (String) b.get("status");
                        
                        // PENDING reservations come first
                        if ("PENDING".equals(statusA) && !"PENDING".equals(statusB)) {
                            return -1;
                        }
                        if (!"PENDING".equals(statusA) && "PENDING".equals(statusB)) {
                            return 1;
                        }
                        
                        // If both are same status, sort by start time (newer first for processed, older first for pending)
                        String startTimeA = (String) a.get("startTime");
                        String startTimeB = (String) b.get("startTime");
                        
                        if (startTimeA != null && startTimeB != null) {
                            if ("PENDING".equals(statusA)) {
                                // For pending reservations, show older ones first (first come, first served)
                                return startTimeA.compareTo(startTimeB);
                            } else {
                                // For processed reservations, show newer ones first
                                return startTimeB.compareTo(startTimeA);
                            }
                        }
                        
                        return 0;
                    });
                    
                    updateScheduleDisplay();
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "예약 데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void loadPendingRequests() {
        // This method now calls loadAllReservations() to show all reservations
        loadAllReservations();
    }
    
    private void filterReservationsForSelectedDate() {
        if (reservationList == null || reservationList.isEmpty()) {
            showEmptySchedule(true);
            return;
        }
        
        // Format selected date to compare with reservation dates
        SimpleDateFormat dateCompareFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateStr = dateCompareFormat.format(selectedDate.getTime());
        
        // Filter reservations for selected date
        List<Map<String, Object>> filteredReservations = new java.util.ArrayList<>();
        
        for (Map<String, Object> reservation : reservationList) {
            String startTime = (String) reservation.get("startTime");
            if (startTime != null) {
                // Extract date part from startTime (format: "2025-09-16T14:00:00")
                String reservationDateStr = startTime.substring(0, 10); // Get "2025-09-16"
                
                if (selectedDateStr.equals(reservationDateStr)) {
                    filteredReservations.add(reservation);
                }
            }
        }
        
        // Update display with filtered reservations
        if (filteredReservations.isEmpty()) {
            showEmptySchedule(true);
        } else {
            showEmptySchedule(false);
            displayFilteredReservations(filteredReservations);
        }
    }
    
    private void displayFilteredReservations(List<Map<String, Object>> filteredReservations) {
        if (getContext() == null) return;
        
        ReservationListAdapter adapter = new ReservationListAdapter(filteredReservations);
        rvDailySchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDailySchedule.setAdapter(adapter);
    }
    
    private void updateScheduleDisplay() {
        // Update calendar with reservation data
        if (calendarAdapter != null && reservationList != null) {
            calendarAdapter.setReservationData(reservationList);
        }
        
        if (reservationList != null && !reservationList.isEmpty()) {
            // Both guardian and caregiver filter by selected date
            filterReservationsForSelectedDate();
        } else {
            showEmptySchedule(true);
        }
    }
    
    private void setupReservationAdapter() {
        if (reservationList == null || getContext() == null) return;
        
        // 간단한 RecyclerView 어댑터 구현
        ReservationListAdapter adapter = new ReservationListAdapter(reservationList);
        rvDailySchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDailySchedule.setAdapter(adapter);
    }
    
    private class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ViewHolder> {
        private List<Map<String, Object>> reservations;
        
        public ReservationListAdapter(List<Map<String, Object>> reservations) {
            this.reservations = reservations;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> reservation = reservations.get(position);
            
            String patientName = (String) reservation.get("patientName");
            String appointmentType = (String) reservation.get("appointmentType");
            String status = (String) reservation.get("status");
            String startTime = (String) reservation.get("startTime");
            String endTime = (String) reservation.get("endTime");
            String purpose = (String) reservation.get("purpose");
            
            // Set patient name
            holder.tvPatientName.setText(patientName != null ? patientName : "환자명 없음");
            
            // Set appointment type with Korean translation
            String typeText = getAppointmentTypeKorean(appointmentType);
            holder.tvAppointmentType.setText(typeText);
            
            // Set type badge color
            setTypeBadgeColor(holder.tvAppointmentType, appointmentType);
            
            // Set status
            holder.tvStatus.setText(getStatusKorean(status));
            
            // Set time range
            if (startTime != null && endTime != null) {
                try {
                    String timeRange = formatTimeRange(startTime, endTime);
                    holder.tvTime.setText(timeRange);
                } catch (Exception e) {
                    holder.tvTime.setText("시간 정보 없음");
                }
            } else {
                holder.tvTime.setText("시간 정보 없음");
            }
            
            // Set purpose
            holder.tvPurpose.setText(purpose != null && !purpose.isEmpty() ? purpose : "목적 정보 없음");
            
            // Show guardian actions only for guardians and their own reservations
            if ("guardian".equals(userRole)) {
                Bundle args = getArguments();
                Long currentGuardianId = args != null ? args.getLong("guardian_id", 0L) : 0L;
                Object reservationGuardianIdObj = reservation.get("guardianId");
                
                // Handle different numeric types from JSON
                Long reservationGuardianId = null;
                if (reservationGuardianIdObj instanceof Number) {
                    reservationGuardianId = ((Number) reservationGuardianIdObj).longValue();
                }
                
                if (reservationGuardianId != null && 
                    currentGuardianId.equals(reservationGuardianId) &&
                    "PENDING".equals(status)) {
                    holder.layoutGuardianActions.setVisibility(View.VISIBLE);
                    holder.btnCancelReservation.setOnClickListener(v -> {
                        showCancelConfirmDialog(reservation, position);
                    });
                } else {
                    holder.layoutGuardianActions.setVisibility(View.GONE);
                }
            } else {
                holder.layoutGuardianActions.setVisibility(View.GONE);
            }
        }
        
        @Override
        public int getItemCount() {
            return reservations != null ? reservations.size() : 0;
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAppointmentType, tvStatus, tvTime, tvPatientName, tvPurpose;
            LinearLayout layoutGuardianActions;
            Button btnCancelReservation;
            
            ViewHolder(View itemView) {
                super(itemView);
                tvAppointmentType = itemView.findViewById(R.id.tv_appointment_type);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvPatientName = itemView.findViewById(R.id.tv_patient_name);
                tvPurpose = itemView.findViewById(R.id.tv_purpose);
                layoutGuardianActions = itemView.findViewById(R.id.layout_guardian_actions);
                btnCancelReservation = itemView.findViewById(R.id.btn_cancel_reservation);
                
                // Add click listener to the whole item
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && reservations != null && position < reservations.size()) {
                        Map<String, Object> reservation = reservations.get(position);
                        showReservationDetailDialog(reservation);
                    }
                });
            }
        }
    }
    
    
    private void showEmptySchedule(boolean show) {
        if (show) {
            rvDailySchedule.setVisibility(View.GONE);
            layoutEmptySchedule.setVisibility(View.VISIBLE);
        } else {
            rvDailySchedule.setVisibility(View.VISIBLE);
            layoutEmptySchedule.setVisibility(View.GONE);
        }
    }
    
    private String getAppointmentTypeKorean(String appointmentType) {
        if (appointmentType == null) return "기타";
        
        switch (appointmentType) {
            case "VISIT": return "상담";
            case "OUTING": return "외출";
            case "OVERNIGHT": return "외박";
            case "CONSULTATION": return "상담";
            default: return appointmentType;
        }
    }
    
    private String getStatusKorean(String status) {
        if (status == null) return "상태 불명";
        
        switch (status) {
            case "PENDING": return "승인 대기";
            case "APPROVED": return "승인됨";
            case "REJECTED": return "거절됨";
            case "CANCELLED": return "취소됨";
            case "COMPLETED": return "완료됨";
            default: return status;
        }
    }
    
    private void setTypeBadgeColor(TextView textView, String appointmentType) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setCornerRadius(24f); // 12dp in pixels
        
        int backgroundColor;
        switch (appointmentType) {
            case "VISIT":
                backgroundColor = getResources().getColor(R.color.purple_500);
                break;
            case "OUTING":
                backgroundColor = getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "OVERNIGHT":
                backgroundColor = getResources().getColor(android.R.color.holo_red_dark);
                break;
            case "CONSULTATION":
                backgroundColor = getResources().getColor(android.R.color.holo_blue_dark);
                break;
            default:
                backgroundColor = getResources().getColor(R.color.teal_700);
        }
        
        drawable.setColor(backgroundColor);
        textView.setBackground(drawable);
    }
    
    private String formatTimeRange(String startTime, String endTime) {
        try {
            // Parse ISO datetime to extract time
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            
            Date startDate = inputFormat.parse(startTime);
            Date endDate = inputFormat.parse(endTime);
            
            return timeFormat.format(startDate) + " - " + timeFormat.format(endDate);
        } catch (Exception e) {
            // Fallback: try to extract time substring if format is different
            if (startTime.length() >= 16 && endTime.length() >= 16) {
                return startTime.substring(11, 16) + " - " + endTime.substring(11, 16);
            }
            return "시간 정보 오류";
        }
    }
    
    private void showReservationDetailDialog(Map<String, Object> reservation) {
        if (getContext() == null) return;
        
        String status = (String) reservation.get("status");
        
        if ("caregiver".equals(userRole)) {
            // 요양원 직원인 경우
            if ("PENDING".equals(status)) {
                // 승인 대기 상태 - 승인/거부 다이얼로그
                showReservationApprovalDialog(reservation);
            } else {
                // 승인/거부된 상태 - 읽기 전용 정보창
                showReservationInfoDialog(reservation);
            }
        } else {
            // 보호자인 경우
            if ("PENDING".equals(status)) {
                // 승인 대기 상태 - 수정 가능한 다이얼로그
                showEditableReservationDialog(reservation);
            } else {
                // 승인/거부된 상태 - 읽기 전용 정보창
                showReservationInfoDialog(reservation);
            }
        }
    }
    
    private void showCancelConfirmDialog(Map<String, Object> reservation, int position) {
        if (getContext() == null) return;
        
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("예약 취소")
                .setMessage("정말로 이 예약을 취소하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    cancelReservation(reservation, position);
                })
                .setNegativeButton("아니오", null)
                .show();
    }
    
    private void cancelReservation(Map<String, Object> reservation, int position) {
        Object appointmentIdObj = reservation.get("appointmentId");
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
        
        String cancelReason = "보호자 요청에 의한 취소";
        
        ApiClient.getReservationApiService()
                .cancelReservation(appointmentId, cancelReason)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "예약이 취소되었습니다", Toast.LENGTH_SHORT).show();
                            
                            // Refresh the schedule display
                            Bundle args = getArguments();
                            if (args != null) {
                                Long guardianId = args.getLong("guardian_id", 0L);
                                if (guardianId > 0) {
                                    loadGuardianReservations(guardianId);
                                }
                            }
                        } else {
                            String errorMessage = "예약 취소에 실패했습니다";
                            if (response.code() == 404) {
                                errorMessage = "예약을 찾을 수 없습니다";
                            } else if (response.code() == 403) {
                                errorMessage = "취소 권한이 없습니다";
                            }
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showReservationApprovalDialog(Map<String, Object> reservation) {
        com.example.coderelief.dialogs.ReservationApprovalDialog dialog = 
            com.example.coderelief.dialogs.ReservationApprovalDialog.newInstance(reservation);
        
        dialog.setOnReservationProcessedListener(() -> {
            // Refresh data after approval/rejection
            loadScheduleData();
        });
        
        dialog.show(getParentFragmentManager(), "ReservationApprovalDialog");
    }
    
    private void showReservationInfoDialog(Map<String, Object> reservation) {
        // Use ReservationRequestDialog in read-only mode
        com.example.coderelief.dialogs.ReservationRequestDialog dialog = 
            com.example.coderelief.dialogs.ReservationRequestDialog.newInstanceForInfo(reservation);
        
        dialog.show(getParentFragmentManager(), "ReservationInfoDialog");
    }
    
    private void showEditableReservationDialog(Map<String, Object> reservation) {
        // Use ReservationRequestDialog in editable mode
        Bundle args = getArguments();
        Long guardianId = args != null ? args.getLong("guardian_id", 0L) : 0L;
        
        com.example.coderelief.dialogs.ReservationRequestDialog dialog = 
            com.example.coderelief.dialogs.ReservationRequestDialog.newInstanceForEdit(guardianId, reservation);
        
        dialog.setOnReservationCreatedListener(() -> {
            // Refresh data after edit
            loadGuardianReservations(guardianId);
        });
        
        dialog.show(getParentFragmentManager(), "EditReservationDialog");
    }
    
    private void showAddScheduleDialog() {
        if (getContext() != null && "caregiver".equals(userRole)) {
            com.example.coderelief.dialogs.StaffScheduleDialog dialog = 
                com.example.coderelief.dialogs.StaffScheduleDialog.newInstance();
            
            dialog.setOnScheduleCreatedListener(() -> {
                // Refresh schedule data when new schedule is created
                Log.d("ScheduleFragment", "StaffScheduleDialog에서 일정 생성 완료 - 전체 일정 새로고침");
                loadScheduleData(); // 전체 일정 데이터를 새로고침
            });
            
            dialog.show(getParentFragmentManager(), "StaffScheduleDialog");
        }
    }
    
    private void createQuickSchedule(String scheduleType) {
        if ("상담 예약".equals(scheduleType) && "guardian".equals(userRole)) {
            // Show reservation request dialog for guardians
            showReservationDialog();
        } else {
            // TODO: Create quick schedule with pre-filled type for staff
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), 
                    scheduleType + " 일정 생성: " + dateFormat.format(selectedDate.getTime()), 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
            
            // TODO: Actually create the schedule in database
            // TODO: Refresh the daily schedule list
            // loadDailySchedule();
        }
    }
    
    private void showReservationDialog() {
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(getContext(), "사용자 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Long guardianId = args.getLong("guardian_id", 0L);
        
        if (guardianId == 0L) {
            Toast.makeText(getContext(), "보호자 정보를 확인할 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ReservationRequestDialog dialog = ReservationRequestDialog.newInstance(guardianId);
        dialog.setOnReservationCreatedListener(new ReservationRequestDialog.OnReservationCreatedListener() {
            @Override
            public void onReservationCreated() {
                // Refresh schedule data when reservation is created
                loadScheduleData();
            }
        });
        dialog.show(getParentFragmentManager(), "ReservationRequestDialog");
    }
    
    private void onScheduleClick(int scheduleId) {
        // TODO: Handle schedule item click
        // Options: View details, Edit (if allowed), Delete (if allowed)
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "일정 상세보기 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void highlightReservation() {
        if (highlightReservationId == null || reservationList == null) {
            return;
        }
        
        // 하이라이트할 예약 찾기
        for (Map<String, Object> reservation : reservationList) {
            Object reservationIdObj = reservation.get("reservationId");
            if (reservationIdObj != null) {
                Long reservationId;
                if (reservationIdObj instanceof Double) {
                    reservationId = ((Double) reservationIdObj).longValue();
                } else if (reservationIdObj instanceof Integer) {
                    reservationId = ((Integer) reservationIdObj).longValue();
                } else {
                    continue;
                }
                
                if (reservationId.equals(highlightReservationId)) {
                    // 예약 날짜로 캘린더 이동
                    Object dateObj = reservation.get("reservationDate");
                    if (dateObj != null) {
                        try {
                            String dateStr = dateObj.toString();
                            // 날짜 형식에 따라 파싱 (예: "2025-09-14")
                            String[] dateParts = dateStr.split("-");
                            if (dateParts.length >= 3) {
                                int year = Integer.parseInt(dateParts[0]);
                                int month = Integer.parseInt(dateParts[1]) - 1; // Calendar는 0부터 시작
                                int day = Integer.parseInt(dateParts[2]);
                                
                                // 해당 날짜로 캘린더 이동
                                currentCalendar.set(year, month, 1);
                                selectedDate.set(year, month, day);
                                
                                updateMonthDisplay();
                                updateSelectedDateDisplay();
                                updateCalendar();
                                loadDailySchedule();
                                
                                // 하이라이트 완료 후 초기화
                                highlightReservationId = null;
                                
                                Toast.makeText(getContext(), "해당 예약으로 이동했습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception e) {
                            Log.e("ScheduleFragment", "날짜 파싱 오류", e);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh schedule data when returning to this fragment
        loadScheduleData();
    }
    
    /**
     * 외부에서 호출하여 일정을 새로고침할 수 있는 public 메서드
     */
    public void refreshSchedule() {
        Log.d("ScheduleFragment", "refreshSchedule() 호출됨 - 일정 데이터 새로고침");
        loadScheduleData();
    }
}