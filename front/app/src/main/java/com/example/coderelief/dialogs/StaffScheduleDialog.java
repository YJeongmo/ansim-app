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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ReservationApiService;
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

public class StaffScheduleDialog extends DialogFragment {
    
    private Spinner spinnerPatient, spinnerYear, spinnerMonth, spinnerDay;
    private TimePicker timePickerStart, timePickerEnd;
    private RadioGroup radioGroupType;
    private TextInputEditText etPurpose, etNotes;
    private Button btnSubmit, btnCancel;
    
    private List<Patient> patientList;
    private Patient selectedPatient;
    private OnScheduleCreatedListener onScheduleCreatedListener;
    
    public interface OnScheduleCreatedListener {
        void onScheduleCreated();
    }
    
    public void setOnScheduleCreatedListener(OnScheduleCreatedListener listener) {
        this.onScheduleCreatedListener = listener;
    }
    
    public static StaffScheduleDialog newInstance() {
        return new StaffScheduleDialog();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_staff_schedule, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        loadPatientList();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        spinnerPatient = view.findViewById(R.id.spinner_patient);
        spinnerYear = view.findViewById(R.id.spinner_year);
        spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerDay = view.findViewById(R.id.spinner_day);
        timePickerStart = view.findViewById(R.id.time_picker_start);
        timePickerEnd = view.findViewById(R.id.time_picker_end);
        radioGroupType = view.findViewById(R.id.radio_group_type);
        etPurpose = view.findViewById(R.id.et_purpose);
        etNotes = view.findViewById(R.id.et_notes);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);
        
        // Set default time to business hours
        timePickerStart.setIs24HourView(true); // 24시간 형식으로 설정
        timePickerStart.setHour(14);
        timePickerStart.setMinute(0);
        timePickerEnd.setIs24HourView(true); // 24시간 형식으로 설정
        timePickerEnd.setHour(16);
        timePickerEnd.setMinute(0);
        
        // Setup date spinners
        setupDateSpinners();
        
        patientList = new ArrayList<>();
    }
    
    private void loadPatientList() {
        ApiClient.getApiService().getPatients()
                .enqueue(new Callback<List<Patient>>() {
                    @Override
                    public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            patientList.clear();
                            patientList.addAll(response.body());
                            setupPatientSpinner();
                        } else {
                            Toast.makeText(getContext(), "환자 목록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<Patient>> call, Throwable t) {
                        Toast.makeText(getContext(), "환자 목록 조회 실패: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
        
        spinnerPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < patientList.size()) {
                    selectedPatient = patientList.get(position);
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPatient = null;
            }
        });
        
        if (!patientList.isEmpty()) {
            selectedPatient = patientList.get(0);
        }
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
    
    
    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> submitSchedule());
        btnCancel.setOnClickListener(v -> dismiss());
    }
    
    private void submitSchedule() {
        if (!validateInput()) {
            return;
        }
        
        btnSubmit.setEnabled(false);
        btnSubmit.setText("일정 생성 중...");
        
        Map<String, Object> scheduleRequest = createScheduleRequest();
        
        ApiClient.getReservationApiService()
                .createReservation(scheduleRequest)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("일정 생성");
                        
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "일정이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                            
                            if (onScheduleCreatedListener != null) {
                                onScheduleCreatedListener.onScheduleCreated();
                            }
                            dismiss();
                        } else {
                            String errorMessage = "일정 생성에 실패했습니다.";
                            if (response.code() == 400) {
                                errorMessage = "입력 정보를 확인해주세요.";
                            } else if (response.code() == 409) {
                                errorMessage = "선택한 시간에 이미 일정이 있습니다.";
                            }
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("일정 생성");
                        Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    private boolean validateInput() {
        if (selectedPatient == null) {
            Toast.makeText(getContext(), "환자를 선택해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Purpose is optional, no validation needed
        
        LocalTime startTime = LocalTime.of(timePickerStart.getHour(), timePickerStart.getMinute());
        LocalTime endTime = LocalTime.of(timePickerEnd.getHour(), timePickerEnd.getMinute());
        
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            Toast.makeText(getContext(), "시작 시간은 종료 시간보다 빨라야 합니다", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private Map<String, Object> createScheduleRequest() {
        Map<String, Object> request = new HashMap<>();
        
        int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        int month = spinnerMonth.getSelectedItemPosition() + 1; // Spinner position is 0-based
        int day = Integer.parseInt(spinnerDay.getSelectedItem().toString());
        LocalDate selectedDate = LocalDate.of(year, month, day);
        
        LocalTime startTime = LocalTime.of(timePickerStart.getHour(), timePickerStart.getMinute());
        LocalTime endTime = LocalTime.of(timePickerEnd.getHour(), timePickerEnd.getMinute());
        
        LocalDateTime startDateTime = LocalDateTime.of(selectedDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(selectedDate, endTime);
        
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
        request.put("appointmentType", appointmentType);
        request.put("startTime", startDateTime.toString());
        request.put("endTime", endDateTime.toString());
        request.put("purpose", etPurpose.getText().toString().trim());
        request.put("staffNotes", etNotes.getText() != null ? etNotes.getText().toString().trim() : "");
        request.put("status", "APPROVED");
        
        return request;
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