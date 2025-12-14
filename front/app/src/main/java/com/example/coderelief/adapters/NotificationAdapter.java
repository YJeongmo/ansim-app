package com.example.coderelief.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.models.Notification;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 알림 목록을 위한 RecyclerView Adapter
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnNotificationClickListener clickListener;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm");

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notifications, OnNotificationClickListener clickListener) {
        this.notifications = notifications;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvType.setText(notification.getNotificationTypeKorean());
        
        // 시간 표시
        if (notification.getCreatedAt() != null) {
            holder.tvTime.setText(notification.getCreatedAt().format(dateFormatter));
        } else {
            holder.tvTime.setText("");
        }
        
        // 타입에 따른 배경색 설정
        setTypeBackground(holder.tvType, notification.getNotificationType());
        
        // 읽지 않은 알림 표시
        if (holder.unreadIndicator != null) {
            holder.unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);
        }
        
        // 읽지 않은 알림은 배경색을 살짝 다르게 표시 (선택사항)
        if (!notification.isRead()) {
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#FFF9F9F9"));
        } else {
            holder.itemView.setBackgroundColor(android.graphics.Color.WHITE);
        }
        
        // 클릭 이벤트
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    private void setTypeBackground(TextView textView, String notificationType) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setCornerRadius(16f);
        
        int backgroundColor;
        int textColor = android.graphics.Color.WHITE;
        
        switch (notificationType) {
            case "CHAT":
                backgroundColor = android.graphics.Color.parseColor("#4CAF50"); // 녹색
                break;
            case "MEAL":
                backgroundColor = android.graphics.Color.parseColor("#FF9800"); // 주황색
                break;
            case "ACTIVITY":
                backgroundColor = android.graphics.Color.parseColor("#2196F3"); // 파랑색
                break;
            case "NOTICE":
                backgroundColor = android.graphics.Color.parseColor("#9C27B0"); // 보라색
                break;
            case "APPOINTMENT":
                backgroundColor = android.graphics.Color.parseColor("#F44336"); // 빨강색
                break;
            case "CONSULTATION":
                backgroundColor = android.graphics.Color.parseColor("#795548"); // 갈색
                break;
            default:
                backgroundColor = android.graphics.Color.parseColor("#607D8B"); // 회색
        }
        
        drawable.setColor(backgroundColor);
        textView.setBackground(drawable);
        textView.setTextColor(textColor);
    }

    public void updateNotifications(List<Notification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvType, tvTime;
        View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvType = itemView.findViewById(R.id.tv_notification_type);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
        }
    }
}