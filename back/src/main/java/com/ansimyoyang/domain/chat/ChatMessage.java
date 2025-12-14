package com.ansimyoyang.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @JsonIgnoreProperties({"messages", "participants"})
    private ChatRoom chatRoom;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
    private String messageText;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    public enum SenderType {
        GUARDIAN, CAREGIVER
    }

    public enum MessageType {
        TEXT, IMAGE, FILE
    }

    // 편의 메서드
    public boolean isFromGuardian() {
        return SenderType.GUARDIAN.equals(senderType);
    }

    public boolean isFromCaregiver() {
        return SenderType.CAREGIVER.equals(senderType);
    }

    // JSON 직렬화를 위한 메서드
    public Long getChatRoomId() {
        return chatRoom != null ? chatRoom.getChatRoomId() : null;
    }

    public void setChatRoomId(Long chatRoomId) {
        if (chatRoom == null) {
            chatRoom = new ChatRoom();
        }
        chatRoom.setChatRoomId(chatRoomId);
    }
}


