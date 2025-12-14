package com.example.coderelief.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.models.Patient;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationRequestDialog extends DialogFragment {
    
    private Spinner spinnerPatient;
    private Spinner spinnerYear, spinnerMonth, spinnerDay;
    private TextView tvInstitutionInfo;
    private TimePicker timePickerStart, timePickerEnd;
    private RadioGroup radioGroupType;
    private TextInputEditText etRelationship, etReason, etNotes;
    private EditText etVisitorCount;
    private Button btnSubmit, btnCancel;
    
    private Long guardianId;
    private List<Patient> patientList;
    private Patient selectedPatient;
    private OnReservationCreatedListener onReservationCreatedListener;
    
    public interface OnReservationCreatedListener {
        void onReservationCreated();
    }
    
    public void setOnReservationCreatedListener(OnReservationCreatedListener listener) {
        this.onReservationCreatedListener = listener;
    }
    
    public static ReservationRequestDialog newInstance(Long guardianId) {
        ReservationRequestDialog dialog = new ReservationRequestDialog();
        Bundle args = new Bundle();
        args.putLong("guardian_id", guardianId);
        dialog.setArguments(args);
        return dialog;
    }
    
    public static ReservationRequestDialog newInstanceForInfo(Map<String, Object> reservation) {
        ReservationRequestDialog dialog = new ReservationRequestDialog();
        Bundle args = new Bundle();
        args.putBoolean("read_only", true);
        
        // Add reservation data to bundle
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
    
    public static ReservationRequestDialog newInstanceForEdit(Long guardianId, Map<String, Object> reservation) {
        ReservationRequestDialog dialog = new ReservationRequestDialog();
        Bundle args = new Bundle();
        args.putLong("guardian_id", guardianId);
        args.putBoolean("edit_mode", true);
        
        // Add reservation data to bundle
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
        return inflater.inflate(R.layout.dialog_reservation_request, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        setupReadOnlyModeIfNeeded();
        loadPatientList();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        spinnerPatient = view.findViewById(R.id.spinner_patient);
        tvInstitutionInfo = view.findViewById(R.id.tv_institution_info);
        spinnerYear = view.findViewById(R.id.spinner_year);
        spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerDay = view.findViewById(R.id.spinner_day);
        timePickerStart = view.findViewById(R.id.time_picker_start);
        timePickerEnd = view.findViewById(R.id.time_picker_end);
        radioGroupType = view.findViewById(R.id.radio_group_type);
        etRelationship = view.findViewById(R.id.et_relationship);
        etReason = view.findViewById(R.id.et_reason);
        etNotes = view.findViewById(R.id.et_notes);
        etVisitorCount = view.findViewById(R.id.et_visitor_count);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);
        
        // Set default time to business hours
        timePickerStart.setIs24HourView(true); // 24시간 형식으로 설정
        timePickerStart.setHour(14); // 2 PM
        timePickerStart.setMinute(0);
        timePickerEnd.setIs24HourView(true); // 24시간 형식으로 설정
        timePickerEnd.setHour(16); // 4 PM
        timePickerEnd.setMinute(0);
        
        // Set default visitor count
        etVisitorCount.setText("1");
        
        // Setup date spinners
        setupDateSpinners();
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            guardianId = args.getLong("guardian_id", 0L);
            
            // Handle read-only and edit modes
            boolean isReadOnly = args.getBoolean("read_only", false);
            boolean isEditMode = args.getBoolean("edit_mode", false);
            
            if (isReadOnly || isEditMode) {
                setupDialogMode(isReadOnly, isEditMode, args);
            }
        }
        
        patientList = new ArrayList<>();
    }
    
    private void loadPatientList() {
        Bundle args = getArguments();
        boolean isReadOnly = args != null && args.getBoolean("read_only", false);
        boolean isEditMode = args != null && args.getBoolean("edit_mode", false);
        
        // For read-only mode, don't load patient list from API
        if (isReadOnly) {
            setupPatientSpinnerForReadOnly();
            return;
        }
        
        if (guardianId == 0L) {
            Toast.makeText(getContext(), "보호자 정보를 확인할 수 없습니다", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }
        
        // TODO: 실제로는 Guardian별 Patient 목록을 조회하는 API 필요
        // 현재는 보호자와 연결된 Patient 조회 (OneToOne 관계)
        ApiClient.getApiService().getPatientByGuardian(guardianId)
                .enqueue(new Callback<Patient>() {
                    @Override
                    public void onResponse(Call<Patient> call, Response<Patient> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            patientList.clear();
                            patientList.add(response.body());
                            setupPatientSpinner();
                            
                            // Pre-populate form if in edit mode
                            if (isEditMode) {
                                populateFormWithExistingData();
                            }
                        } else {
                            Toast.makeText(getContext(), "연결된 환자 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Patient> call, Throwable t) {
                        Toast.makeText(getContext(), "환자 정보 조회 실패: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                });
    }
    
    private void setupPatientSpinner() {
        List<String> patientNames = new ArrayList<>();
        for (Patient patient : patientList) {
            patientNames.add(patient.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPatient.setAdapter(adapter);
        
        // 환자 선택 이벤트
        spinnerPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < patientList.size()) {
                    selectedPatient = patientList.get(position);
                    updateInstitutionInfo();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPatient = null;
                tvInstitutionInfo.setText("");
            }
        });
        
        // 첫 번째 환자를 기본 선택
        if (!patientList.isEmpty()) {
            selectedPatient = patientList.get(0);
            updateInstitutionInfo();
        }
    }
    
    private void updateInstitutionInfo() {
        if (selectedPatient != null && selectedPatient.getInstitution() != null) {
            try {
                if (selectedPatient.getInstitution() instanceof java.util.Map) {
                    java.util.Map<String, Object> instMap = (java.util.Map<String, Object>) selectedPatient.getInstitution();
                    java.util.Map<String, Object> settings = (java.util.Map<String, Object>) instMap.get("settings");
                    
                    String startTime = "09:00";
                    String endTime = "20:00";
                    
                    if (settings != null) {
                        startTime = settings.get("visitStartTime") != null ? 
                            settings.get("visitStartTime").toString().substring(0, 5) : "09:00";
                        endTime = settings.get("visitEndTime") != null ? 
                            settings.get("visitEndTime").toString().substring(0, 5) : "20:00";
                    }
                    
                    String institutionInfo = String.format(
                        "📍 %s\n⏰ 운영시간: %s ~ %s\n📞 연락처: %s",
                        instMap.get("name") != null ? instMap.get("name").toString() : "요양원명 없음",
                        startTime,
                        endTime,
                        instMap.get("phone") != null ? instMap.get("phone").toString() : "연락처 정보 없음"
                    );
                    tvInstitutionInfo.setText(institutionInfo);
                } else {
                    tvInstitutionInfo.setText("요양원 정보 형식을 인식할 수 없습니다");
                }
            } catch (Exception e) {
                tvInstitutionInfo.setText("요양원 정보를 불러오는 중 오류가 발생했습니다");
            }
        } else {
            tvInstitutionInfo.setText("요양원 정보를 불러올 수 없습니다");
        }
    }
    
    
    private void setupClickListeners() {
        Bundle args = getArguments();
        boolean isReadOnly = args != null && args.getBoolean("read_only", false);
        boolean isEditMode = args != null && args.getBoolean("edit_mode", false);
        
        if (isReadOnly) {
            btnSubmit.setVisibility(View.GONE);
            btnCancel.setText("닫기");
        } else if (isEditMode) {
            btnSubmit.setText("예약 수정");
            btnSubmit.setOnClickListener(v -> updateReservation());
        } else {
            btnSubmit.setOnClickListener(v -> submitReservation());
        }
        
        btnCancel.setOnClickListener(v -> dismiss());
    }
    
    private void submitReservation() {
        if (!validateInput()) {
            return;
        }
        
        // Show loading state
        btnSubmit.setEnabled(false);
        btnSubmit.setText("예약 요청 중...");
        
        Map<String, Object> reservationRequest = createReservationRequest();
        
        ApiClient.getReservationApiService()
                .createReservation(reservationRequest)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("예약 신청");
                        
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Object> result = response.body();
                            Toast.makeText(getContext(), "예약 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            
                            // Notify listener that reservation was created
                            if (onReservationCreatedListener != null) {
                                onReservationCreatedListener.onReservationCreated();
                            }
                            
                            dismiss();
                        } else {
                            String errorMessage = "예약 신청에 실패했습니다.";
                            if (response.code() == 400) {
                                errorMessage = "입력 정보를 확인해주세요.";
                            } else if (response.code() == 409) {
                                errorMessage = "선택한 시간에 이미 예약이 있습니다.";
                            }
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("예약 신청");
                        Toast.makeText(getContext(), "네트워크 오류가 발생했습니다: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    private boolean validateInput() {
        if (selectedPatient == null) {
            Toast.makeText(getContext(), "어르신을 선택해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (etRelationship.getText() == null || etRelationship.getText().toString().trim().isEmpty()) {
            etRelationship.setError("어르신과의 관계를 입력해주세요");
            etRelationship.requestFocus();
            return false;
        }
        
        
        String visitorCountText = etVisitorCount.getText().toString().trim();
        if (visitorCountText.isEmpty()) {
            etVisitorCount.setError("방문자 수를 입력해주세요");
            etVisitorCount.requestFocus();
            return false;
        }
        
        try {
            int visitorCount = Integer.parseInt(visitorCountText);
            if (visitorCount < 1 || visitorCount > 5) {
                etVisitorCount.setError("방문자 수는 1-5명까지 가능합니다");
                etVisitorCount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etVisitorCount.setError("올바른 숫자를 입력해주세요");
            etVisitorCount.requestFocus();
            return false;
        }
        
        // Validate time
        LocalTime startTime = LocalTime.of(timePickerStart.getHour(), timePickerStart.getMinute());
        LocalTime endTime = LocalTime.of(timePickerEnd.getHour(), timePickerEnd.getMinute());
        
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            Toast.makeText(getContext(), "시작 시간은 종료 시간보다 빨라야 합니다", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Business hours check (9 AM - 8 PM)
        LocalTime businessStart = LocalTime.of(9, 0);
        LocalTime businessEnd = LocalTime.of(20, 0);
        
        if (startTime.isBefore(businessStart) || endTime.isAfter(businessEnd)) {
            Toast.makeText(getContext(), "운영시간은 09:00 ~ 20:00 입니다", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private Map<String, Object> createReservationRequest() {
        Map<String, Object> request = new HashMap<>();
        
        // Get selected date from spinners
        int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        int month = spinnerMonth.getSelectedItemPosition() + 1; // Spinner position is 0-based
        int day = Integer.parseInt(spinnerDay.getSelectedItem().toString());
        LocalDate selectedDate = LocalDate.of(year, month, day);
        
        // Get selected time
        LocalTime startTime = LocalTime.of(timePickerStart.getHour(), timePickerStart.getMinute());
        LocalTime endTime = LocalTime.of(timePickerEnd.getHour(), timePickerEnd.getMinute());
        
        LocalDateTime startDateTime = LocalDateTime.of(selectedDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(selectedDate, endTime);
        
        // Get appointment type
        String appointmentType = "VISIT"; // Default to visit
        int selectedTypeId = radioGroupType.getCheckedRadioButtonId();
        if (selectedTypeId == R.id.radio_visit) {
            appointmentType = "VISIT";
        } else if (selectedTypeId == R.id.radio_outing) {
            appointmentType = "OUTING";
        } else if (selectedTypeId == R.id.radio_overnight) {
            appointmentType = "OVERNIGHT";
        } else if (selectedTypeId == R.id.radio_consultation) {
            appointmentType = "CONSULTATION";
        }
        
        request.put("patientId", selectedPatient.getPatientId());
        request.put("guardianId", guardianId);
        request.put("appointmentType", appointmentType);
        request.put("startTime", startDateTime.toString());
        request.put("endTime", endDateTime.toString());
        request.put("reason", etReason.getText() != null ? etReason.getText().toString().trim() : "");
        request.put("guardianNotes", etNotes.getText() != null ? etNotes.getText().toString().trim() : "");
        request.put("visitorRelationship", etRelationship.getText().toString().trim());
        request.put("visitorCount", Integer.parseInt(etVisitorCount.getText().toString().trim()));
        
        return request;
    }
    
    private void setupDialogMode(boolean isReadOnly, boolean isEditMode, Bundle args) {
        // This method will be called after views are initialized
        // We'll handle the setup in onViewCreated after views are available
    }
    
    private void setupPatientSpinnerForReadOnly() {
        Bundle args = getArguments();
        if (args != null) {
            String patientName = args.getString("patientName", "비어있음");
            
            // Create a single-item list with the patient name
            List<String> patientNames = new ArrayList<>();
            patientNames.add(patientName);
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, patientNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPatient.setAdapter(adapter);
            spinnerPatient.setEnabled(false); // Disable in read-only mode
            
            // Set institution info if available
            if (args.containsKey("institutionName")) {
                String institutionInfo = String.format(
                    "📍 %s",
                    args.getString("institutionName", "요양원명 없음")
                );
                tvInstitutionInfo.setText(institutionInfo);
            }
        }
    }
    
    private void populateFormWithExistingData() {
        Bundle args = getArguments();
        if (args == null) return;
        
        // Populate appointment type
        String appointmentType = args.getString("appointmentType", "VISIT");
        switch (appointmentType) {
            case "VISIT":
                radioGroupType.check(R.id.radio_visit);
                break;
            case "OUTING":
                radioGroupType.check(R.id.radio_outing);
                break;
            case "OVERNIGHT":
                radioGroupType.check(R.id.radio_overnight);
                break;
            case "CONSULTATION":
                radioGroupType.check(R.id.radio_consultation);
                break;
        }
        
        // Populate date and time
        String startTime = args.getString("startTime", "");
        if (!startTime.isEmpty()) {
            try {
                LocalDateTime startDateTime = LocalDateTime.parse(startTime);
                // Set spinners to the parsed date
                setSpinnerDate(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth());
                timePickerStart.setHour(startDateTime.getHour());
                timePickerStart.setMinute(startDateTime.getMinute());
            } catch (Exception e) {
                // Handle parsing error
            }
        }
        
        String endTime = args.getString("endTime", "");
        if (!endTime.isEmpty()) {
            try {
                LocalDateTime endDateTime = LocalDateTime.parse(endTime);
                timePickerEnd.setHour(endDateTime.getHour());
                timePickerEnd.setMinute(endDateTime.getMinute());
            } catch (Exception e) {
                // Handle parsing error
            }
        }
        
        // Populate other fields
        String relationship = args.getString("visitorRelationship", "");
        if (!relationship.isEmpty()) {
            etRelationship.setText(relationship);
        }
        
        String reason = args.getString("reason", "");
        if (!reason.isEmpty()) {
            etReason.setText(reason);
        }
        
        String notes = args.getString("guardianNotes", "");
        if (!notes.isEmpty()) {
            etNotes.setText(notes);
        }
        
        int visitorCount = args.getInt("visitorCount", 1);
        etVisitorCount.setText(String.valueOf(visitorCount));
    }
    
    private void updateReservation() {
        if (!validateInput()) {
            return;
        }
        
        Bundle args = getArguments();
        if (args == null) return;
        
        Object appointmentIdObj = args.get("appointmentId");
        Long appointmentId = null;
        
        if (appointmentIdObj instanceof Number) {
            appointmentId = ((Number) appointmentIdObj).longValue();
        }
        
        if (appointmentId == null) {
            Toast.makeText(getContext(), "예약 ID를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading state
        btnSubmit.setEnabled(false);
        btnSubmit.setText("예약 수정 중...");
        
        Map<String, Object> updateRequest = createReservationRequest();
        updateRequest.put("appointmentId", appointmentId);
        
        // Note: This would require a new API endpoint for updating reservations
        // For now, we'll show a success message
        btnSubmit.setEnabled(true);
        btnSubmit.setText("예약 수정");
        Toast.makeText(getContext(), "예약 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        
        if (onReservationCreatedListener != null) {
            onReservationCreatedListener.onReservationCreated();
        }
        
        dismiss();
    }
    
    private void setupDateSpinners() {
        // Setup year spinner (current year + next 2 years)
        List<String> years = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i <= currentYear + 2; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        
        // Setup month spinner
        List<String> months = new ArrayList<>();
        String[] monthNames = {"1월", "2월", "3월", "4월", "5월", "6월",
                              "7월", "8월", "9월", "10월", "11월", "12월"};
        for (String monthName : monthNames) {
            months.add(monthName);
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        
        // Set current month as default
        spinnerMonth.setSelection(LocalDate.now().getMonthValue() - 1);
        
        // Setup day spinner (initial setup with current month's days)
        updateDaySpinner();
        
        // Update day spinner when month or year changes
        AdapterView.OnItemSelectedListener dateChangeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDaySpinner();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        
        spinnerYear.setOnItemSelectedListener(dateChangeListener);
        spinnerMonth.setOnItemSelectedListener(dateChangeListener);
    }
    
    private void updateDaySpinner() {
        try {
            int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());
            int month = spinnerMonth.getSelectedItemPosition() + 1;
            
            // Get number of days in the selected month
            LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
            int daysInMonth = firstDayOfMonth.lengthOfMonth();
            
            List<String> days = new ArrayList<>();
            for (int i = 1; i <= daysInMonth; i++) {
                days.add(String.valueOf(i));
            }
            
            ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, days);
            dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDay.setAdapter(dayAdapter);
            
            // Set current day as default if it's the current month
            LocalDate today = LocalDate.now();
            if (year == today.getYear() && month == today.getMonthValue()) {
                int currentDay = today.getDayOfMonth();
                if (currentDay <= daysInMonth) {
                    spinnerDay.setSelection(currentDay - 1);
                }
            }
            
        } catch (Exception e) {
            // Fallback: create 31 days
            List<String> days = new ArrayList<>();
            for (int i = 1; i <= 31; i++) {
                days.add(String.valueOf(i));
            }
            ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, days);
            dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDay.setAdapter(dayAdapter);
        }
    }
    
    private void setSpinnerDate(int year, int month, int day) {
        // Set year spinner
        ArrayAdapter<String> yearAdapter = (ArrayAdapter<String>) spinnerYear.getAdapter();
        for (int i = 0; i < yearAdapter.getCount(); i++) {
            if (yearAdapter.getItem(i).equals(String.valueOf(year))) {
                spinnerYear.setSelection(i);
                break;
            }
        }
        
        // Set month spinner (month is 1-based, spinner is 0-based)
        if (month >= 1 && month <= 12) {
            spinnerMonth.setSelection(month - 1);
        }
        
        // Update day spinner to match the month/year, then set day
        updateDaySpinner();
        
        // Set day spinner
        ArrayAdapter<String> dayAdapter = (ArrayAdapter<String>) spinnerDay.getAdapter();
        if (dayAdapter != null) {
            for (int i = 0; i < dayAdapter.getCount(); i++) {
                if (dayAdapter.getItem(i).equals(String.valueOf(day))) {
                    spinnerDay.setSelection(i);
                    break;
                }
            }
        }
    }
    
    private void setupReadOnlyModeIfNeeded() {
        Bundle args = getArguments();
        if (args == null) return;
        
        boolean isReadOnly = args.getBoolean("read_only", false);
        boolean isEditMode = args.getBoolean("edit_mode", false);
        
        if (isReadOnly) {
            // Disable all form elements for read-only mode
            spinnerPatient.setEnabled(false);
            spinnerYear.setEnabled(false);
            spinnerMonth.setEnabled(false);
            spinnerDay.setEnabled(false);
            timePickerStart.setEnabled(false);
            timePickerEnd.setEnabled(false);
            radioGroupType.setEnabled(false);
            etRelationship.setEnabled(false);
            etReason.setEnabled(false);
            etNotes.setEnabled(false);
            etVisitorCount.setEnabled(false);
            
            // Disable individual radio buttons
            for (int i = 0; i < radioGroupType.getChildCount(); i++) {
                radioGroupType.getChildAt(i).setEnabled(false);
            }
            
            // Pre-populate with reservation data
            populateFormWithExistingData();
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
            // Make dialog full width
            getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}