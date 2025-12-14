package com.example.coderelief.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.models.CareCenter;

import java.util.List;

public class CareCenterAdapter extends RecyclerView.Adapter<CareCenterAdapter.CareCenterViewHolder> {

    private List<CareCenter> careCenters;
    private OnCareCenterActionListener listener;

    public interface OnCareCenterActionListener {
        void onCareCenterClicked(CareCenter careCenter);
    }

    public CareCenterAdapter(List<CareCenter> careCenters, OnCareCenterActionListener listener) {
        this.careCenters = careCenters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CareCenterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_care_center, parent, false);
        return new CareCenterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CareCenterViewHolder holder, int position) {
        CareCenter careCenter = careCenters.get(position);
        holder.bind(careCenter);
    }

    @Override
    public int getItemCount() {
        return careCenters != null ? careCenters.size() : 0;
    }

    public void updateCareCenters(List<CareCenter> newCareCenters) {
        this.careCenters = newCareCenters;
        notifyDataSetChanged();
    }

    class CareCenterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCareCenterName;
        private TextView tvAddress;
        private TextView tvDistance;
        private TextView tvPhone;

        public CareCenterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCareCenterName = itemView.findViewById(R.id.tv_care_center_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvPhone = itemView.findViewById(R.id.tv_phone);

            // 전체 카드 클릭 이벤트
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCareCenterClicked(careCenters.get(position));
                }
            });
        }

        public void bind(CareCenter careCenter) {
            tvCareCenterName.setText(careCenter.getName());
            tvAddress.setText(careCenter.getAddress());
            tvDistance.setText(careCenter.getFormattedDistance());
            
            // 전화번호 표시
            String phone = careCenter.getPhone();
            android.util.Log.d("CareCenterAdapter", "요양원: " + careCenter.getName() + ", 전화번호: '" + phone + "'");
            
            if (phone != null && !phone.isEmpty()) {
                tvPhone.setText(phone);
                tvPhone.setVisibility(View.VISIBLE);
                android.util.Log.d("CareCenterAdapter", "전화번호 표시됨: " + phone);
            } else {
                tvPhone.setVisibility(View.GONE);
                android.util.Log.d("CareCenterAdapter", "전화번호 없음 - 숨김 처리");
            }
        }
    }
}