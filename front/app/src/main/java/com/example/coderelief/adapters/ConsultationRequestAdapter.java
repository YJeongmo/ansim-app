package com.example.coderelief.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.models.ConsultationRequest;

import java.util.List;

public class ConsultationRequestAdapter extends RecyclerView.Adapter<ConsultationRequestAdapter.ViewHolder> {
    
    private List<ConsultationRequest> consultationRequests;
    private OnConsultationRequestClickListener listener;
    
    public interface OnConsultationRequestClickListener {
        void onConsultationRequestClick(ConsultationRequest request);
    }
    
    public ConsultationRequestAdapter(List<ConsultationRequest> consultationRequests, OnConsultationRequestClickListener listener) {
        this.consultationRequests = consultationRequests;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consultation_request, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsultationRequest request = consultationRequests.get(position);
        holder.bind(request);
    }
    
    @Override
    public int getItemCount() {
        return consultationRequests.size();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvInstitutionName, tvApplicantName, tvApplicantPhone;
        private TextView tvConsultationPurpose, tvStatus, tvCreatedAt;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvInstitutionName = itemView.findViewById(R.id.tvInstitutionName);
            tvApplicantName = itemView.findViewById(R.id.tvApplicantName);
            tvApplicantPhone = itemView.findViewById(R.id.tvApplicantPhone);
            tvConsultationPurpose = itemView.findViewById(R.id.tvConsultationPurpose);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onConsultationRequestClick(consultationRequests.get(position));
                }
            });
        }
        
        public void bind(ConsultationRequest request) {
            tvInstitutionName.setText(request.getInstitutionName());
            tvApplicantName.setText(request.getApplicantName());
            tvApplicantPhone.setText(request.getApplicantPhone());
            tvConsultationPurpose.setText(request.getConsultationPurpose());
            tvStatus.setText("상담 신청");
            tvCreatedAt.setText(String.format("신청일: %s", request.getCreatedAtString()));
            
            // 기본 색상 설정
            setDefaultColor();
        }
        
        private void setDefaultColor() {
            tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
        }
    }
}




