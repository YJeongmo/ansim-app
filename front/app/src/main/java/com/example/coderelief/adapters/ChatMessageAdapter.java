package com.example.coderelief.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    private List<ChatMessage> messages;
    private String currentUserType; // "GUARDIAN" or "CAREGIVER"
    private Long currentUserId;

    public ChatMessageAdapter(String currentUserType, Long currentUserId) {
        this.messages = new ArrayList<>();
        this.currentUserType = currentUserType;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        
        // 내가 보낸 메시지인지 확인
        if (isMyMessage(message)) {
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OTHER_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            View view = inflater.inflate(R.layout.item_chat_message_my, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_message_other, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(message);
        } else if (holder instanceof OtherMessageViewHolder) {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void addMessages(List<ChatMessage> newMessages) {
        int startPosition = messages.size();
        this.messages.addAll(newMessages);
        notifyItemRangeInserted(startPosition, newMessages.size());
    }

    private boolean isMyMessage(ChatMessage message) {
        // null 체크 추가
        if (currentUserType == null || message.getSenderType() == null || 
            currentUserId == null || message.getSenderId() == null) {
            return false;
        }
        
        // 현재 사용자 타입과 메시지 발신자 타입이 같고, 사용자 ID도 같으면 내 메시지
        return currentUserType.equals(message.getSenderType()) && 
               currentUserId.equals(message.getSenderId());
    }

    // 내 메시지 ViewHolder
    public static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageText;
        private TextView tvMessageTime;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageText = itemView.findViewById(R.id.tv_message_text);
            tvMessageTime = itemView.findViewById(R.id.tv_message_time);
        }

        public void bind(ChatMessage message) {
            tvMessageText.setText(message.getMessageText());
            tvMessageTime.setText(message.getFormattedTime());
        }
    }

    // 상대방 메시지 ViewHolder
    public static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageText;
        private TextView tvMessageTime;
        private TextView tvSenderName;

        public OtherMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageText = itemView.findViewById(R.id.tv_message_text);
            tvMessageTime = itemView.findViewById(R.id.tv_message_time);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
        }

        public void bind(ChatMessage message) {
            tvMessageText.setText(message.getMessageText());
            tvMessageTime.setText(message.getFormattedTime());
            
            // 발신자 이름 설정
            String senderName = message.isFromGuardian() ? "보호자" : "요양보호사";
            tvSenderName.setText(senderName);
        }
    }
}


