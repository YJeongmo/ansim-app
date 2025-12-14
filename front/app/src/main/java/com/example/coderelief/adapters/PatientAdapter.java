package com.example.coderelief.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.models.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * 환자 목록을 표시하는 RecyclerView 어댑터
 * 스키마 기반 Patient 모델 사용
 */
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private static final String TAG = "PatientAdapter";
    private List<Patient> patients = new ArrayList<>();
    private OnPatientActionListener actionListener;

    // 클릭 이벤트 인터페이스
    public interface OnPatientActionListener {
        void onPatientDetailClick(Patient patient);
        void onContactGuardianClick(Patient patient);
    }

    public PatientAdapter(OnPatientActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.bind(patient);
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    /**
     * 환자 목록 업데이트
     */
    public void updatePatients(List<Patient> newPatients) {
        Log.d(TAG, "환자 목록 업데이트 시작: 기존 " + patients.size() + "명, 새로운 " + (newPatients != null ? newPatients.size() : 0) + "명");
        
        this.patients.clear();
        if (newPatients != null) {
            this.patients.addAll(newPatients);
            
            // 각 환자 데이터 로그 출력
            for (int i = 0; i < newPatients.size(); i++) {
                Patient patient = newPatients.get(i);
                Log.d(TAG, "환자 " + (i+1) + " 바인딩: " + 
                    "ID=" + patient.getPatientId() + 
                    ", 이름=" + patient.getName() + 
                    ", 나이=" + patient.getAge() + 
                    ", 방호실=" + patient.getRoomNumber() + 
                    ", 요양등급=" + patient.getCareLevel());
            }
        }
        
        notifyDataSetChanged();
        Log.d(TAG, "환자 목록 업데이트 완료: 총 " + patients.size() + "명");
    }

    /**
     * 단일 환자 추가
     */
    public void addPatient(Patient patient) {
        if (patient != null) {
            this.patients.add(patient);
            notifyItemInserted(patients.size() - 1);
        }
    }

    /**
     * ViewHolder 클래스
     */
    class PatientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPatientName;
        private TextView tvPatientAge;
        private TextView tvRoomNumber;
        private Button btnPatientDetail;
        private Button btnContactGuardian;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvPatientAge = itemView.findViewById(R.id.tv_patient_age);
            tvRoomNumber = itemView.findViewById(R.id.tv_room_number);
            btnPatientDetail = itemView.findViewById(R.id.btn_patient_detail);
            btnContactGuardian = itemView.findViewById(R.id.btn_contact_guardian);
        }

        public void bind(Patient patient) {
            Log.d(TAG, "ViewHolder 바인딩: " + patient.getName());
            
            // 환자 정보 바인딩
            tvPatientName.setText(patient.getName());
            tvPatientAge.setText(patient.getAge() + "세");
            tvRoomNumber.setText(patient.getRoomNumber() + "호");
            

            // 버튼 클릭 이벤트 설정
            btnPatientDetail.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onPatientDetailClick(patient);
                }
            });

            btnContactGuardian.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onContactGuardianClick(patient);
                }
            });
        }
    }
}