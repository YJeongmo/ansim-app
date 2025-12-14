package com.ansimyoyang.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    @Builder.Default
    private RoomType roomType = RoomType.PATIENT_CARE;

    @Column(name = "guardian_id")
    private Long guardianId;

    @Column(name = "caregiver_id")
    private Long caregiverId;

    @Column(name = "institution_id")
    private Long institutionId;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 채팅 메시지와의 관계 (일대다)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"chatRoom"})
    private List<ChatMessage> messages;

    // 채팅방 참여자와의 관계 (일대다)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"chatRoom"})
    private List<ChatRoomParticipant> participants;

    public enum RoomType {
        PATIENT_CARE
    }

    // 편의 메서드
    public boolean isParticipant(Long userId, String userType) {
        return participants.stream()
                .anyMatch(p -> p.getUserId().equals(userId) && p.getUserType().name().equals(userType));
    }

    public ChatRoomParticipant getParticipant(Long userId, String userType) {
        return participants.stream()
                .filter(p -> p.getUserId().equals(userId) && p.getUserType().name().equals(userType))
                .findFirst()
                .orElse(null);
    }
}


