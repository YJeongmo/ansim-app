package com.example.coderelief.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;

import java.util.List;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.RegionViewHolder> {

    private List<String> regions;
    private OnRegionClickListener listener;

    public interface OnRegionClickListener {
        void onRegionClick(String region, int position);
    }

    public RegionAdapter(List<String> regions, OnRegionClickListener listener) {
        this.regions = regions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RegionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_region, parent, false);
        return new RegionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegionViewHolder holder, int position) {
        String region = regions.get(position);
        holder.bind(region, position);
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    class RegionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRegionName;

        public RegionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRegionName = itemView.findViewById(R.id.tv_region_name);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRegionClick(regions.get(position), position);
                }
            });
        }

        public void bind(String region, int position) {
            tvRegionName.setText(region);
        }
    }
}


