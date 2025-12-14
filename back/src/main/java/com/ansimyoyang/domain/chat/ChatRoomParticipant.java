package com.ansimyoyang.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_participant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @JsonIgnoreProperties({"messages", "participants"})
    private ChatRoom chatRoom;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    public enum UserType {
        GUARDIAN, CAREGIVER
    }

    // 편의 메서드
    public boolean isGuardian() {
        return UserType.GUARDIAN.equals(userType);
    }

    public boolean isCaregiver() {
        return UserType.CAREGIVER.equals(userType);
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


