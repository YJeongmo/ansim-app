package com.example.coderelief.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    
    private List<Map<String, Object>> notices;
    private OnNoticeClickListener listener;
    
    public interface OnNoticeClickListener {
        void onNoticeClick(int noticeId, boolean isImportant);
    }
    
    public NoticeAdapter(List<Map<String, Object>> notices, OnNoticeClickListener listener) {
        this.notices = notices;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false);
        return new NoticeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Map<String, Object> notice = notices.get(position);
        holder.bind(notice);
    }
    
    @Override
    public int getItemCount() {
        return notices != null ? notices.size() : 0;
    }
    
    public void updateNotices(List<Map<String, Object>> newNotices) {
        this.notices = newNotices;
        notifyDataSetChanged();
    }
    
    class NoticeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvContent, tvDate, tvPriority;
        
        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notice_title);
            tvContent = itemView.findViewById(R.id.tv_notice_content);
            tvDate = itemView.findViewById(R.id.tv_notice_date);
            tvPriority = itemView.findViewById(R.id.tv_notice_priority);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Map<String, Object> notice = notices.get(position);
                    
                    // noticeId가 Double로 올 수 있으므로 안전하게 처리
                    Object noticeIdObj = notice.get("noticeId");
                    int noticeId = 0;
                    if (noticeIdObj instanceof Double) {
                        noticeId = ((Double) noticeIdObj).intValue();
                    } else if (noticeIdObj instanceof Integer) {
                        noticeId = (Integer) noticeIdObj;
                    }
                    
                    String priority = (String) notice.get("priority");
                    boolean isImportant = "URGENT".equals(priority) || "IMPORTANT".equals(priority);
                    
                    if (noticeId > 0) {
                        listener.onNoticeClick(noticeId, isImportant);
                    }
                }
            });
        }
        
        public void bind(Map<String, Object> notice) {
            // 제목 설정
            String title = (String) notice.get("title");
            tvTitle.setText(title != null ? title : "제목 없음");
            
            // 내용 설정 (줄임)
            String content = (String) notice.get("content");
            if (content != null && content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            tvContent.setText(content != null ? content : "내용 없음");
            
            // 날짜 설정
            String createdAt = (String) notice.get("createdAt");
            if (createdAt != null) {
                try {
                    // ISO 8601 형식의 날짜를 파싱
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
                    Date date = inputFormat.parse(createdAt);
                    tvDate.setText(outputFormat.format(date));
                } catch (Exception e) {
                    tvDate.setText(createdAt);
                }
            } else {
                tvDate.setText("");
            }
            
            // 우선순위 설정
            String priority = (String) notice.get("priority");
            if (priority != null) {
                switch (priority) {
                    case "URGENT":
                        tvPriority.setText("긴급");
                        tvPriority.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
                        break;
                    case "IMPORTANT":
                        tvPriority.setText("중요");
                        tvPriority.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
                        break;
                    default:
                        tvPriority.setText("일반");
                        tvPriority.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                        break;
                }
            } else {
                tvPriority.setText("일반");
                tvPriority.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
}
