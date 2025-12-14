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
 * 가족 구성원 목록을 표시하는 RecyclerView 어댑터
 * Patient 모델을 사용하여 가족 정보 표시
 */
public class FamilyMemberAdapter extends RecyclerView.Adapter<FamilyMemberAdapter.FamilyMemberViewHolder> {

    private static final String TAG = "FamilyMemberAdapter";
    private List<Patient> familyMembers = new ArrayList<>();
    private OnFamilyActionListener actionListener;

    // 클릭 이벤트 인터페이스
    public interface OnFamilyActionListener {
        void onFamilyDetailClick(Patient familyMember);
        void onChatWithCenterClick(Patient familyMember);
    }

    public FamilyMemberAdapter(OnFamilyActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public FamilyMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_family_member, parent, false);
        return new FamilyMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyMemberViewHolder holder, int position) {
        Patient familyMember = familyMembers.get(position);
        holder.bind(familyMember, actionListener);
    }

    @Override
    public int getItemCount() {
        return familyMembers.size();
    }

    /**
     * 가족 구성원 목록 업데이트
     */
    public void updateFamilyMembers(List<Patient> newFamilyMembers) {
        Log.d(TAG, "가족 구성원 목록 업데이트 시작: 기존 " + familyMembers.size() + "명, 새로운 " + (newFamilyMembers != null ? newFamilyMembers.size() : 0) + "명");
        
        this.familyMembers.clear();
        if (newFamilyMembers != null) {
            this.familyMembers.addAll(newFamilyMembers);
            
            // 각 가족 구성원 데이터 로그 출력
            for (int i = 0; i < newFamilyMembers.size(); i++) {
                Patient familyMember = newFamilyMembers.get(i);
                Log.d(TAG, "가족 구성원 " + (i+1) + " 바인딩: " + 
                    "ID=" + familyMember.getPatientId() + 
                    ", 이름=" + familyMember.getName() + 
                    ", 나이=" + familyMember.getAge() + 
                    ", 방호실=" + familyMember.getRoomNumber() + 
                    ", 요양등급=" + familyMember.getCareLevel());
            }
        }
        
        notifyDataSetChanged();
        Log.d(TAG, "가족 구성원 목록 업데이트 완료: 총 " + familyMembers.size() + "명");
    }

    static class FamilyMemberViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFamilyName, tvFamilyAge, tvRoomNumber, tvCareCenterName;
        private Button btnFamilyDetail, btnChatWithCenter;

        public FamilyMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvFamilyName = itemView.findViewById(R.id.tv_family_name);
            tvFamilyAge = itemView.findViewById(R.id.tv_family_age);
            tvRoomNumber = itemView.findViewById(R.id.tv_room_number);
            tvCareCenterName = itemView.findViewById(R.id.tv_care_center_name);
            btnFamilyDetail = itemView.findViewById(R.id.btn_family_detail);
            btnChatWithCenter = itemView.findViewById(R.id.btn_chat_with_center);
        }

        public void bind(Patient familyMember, OnFamilyActionListener listener) {
            Log.d(TAG, "FamilyMemberViewHolder 바인딩: " + familyMember.getName());
            
            tvFamilyName.setText(familyMember.getName());
            tvFamilyAge.setText(familyMember.getAge() + "세");
            tvRoomNumber.setText(familyMember.getRoomNumber() + "호");
            
            // 가족별 요양원 정보 설정 (실제로는 DB에서 조회)
            String careCenterName = getCareCenterName(familyMember.getName());
            tvCareCenterName.setText("📍 입소 요양원: " + careCenterName);

            btnFamilyDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFamilyDetailClick(familyMember);
                }
            });

            btnChatWithCenter.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatWithCenterClick(familyMember);
                }
            });
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
}