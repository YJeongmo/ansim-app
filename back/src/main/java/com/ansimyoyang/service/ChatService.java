package com.ansimyoyang.service;

import com.ansimyoyang.domain.chat.ChatMessage;
import com.ansimyoyang.domain.chat.ChatRoom;
import com.ansimyoyang.domain.chat.ChatRoomParticipant;
import com.ansimyoyang.repository.chat.ChatMessageRepository;
import com.ansimyoyang.repository.chat.ChatRoomParticipantRepository;
import com.ansimyoyang.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final NotificationService notificationService;

    // 채팅방 생성 또는 조회
    @Transactional
    public ChatRoom getOrCreateChatRoom(Long patientId, String patientName, Long guardianId, Long caregiverId, Long institutionId) {
        log.info("채팅방 조회/생성 요청: patientId={}, guardianId={}, caregiverId={}", patientId, guardianId, caregiverId);
        
        // 기존 채팅방 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByPatientId(patientId);
        
        if (existingRoom.isPresent()) {
            log.info("기존 채팅방 발견: {}", existingRoom.get().getChatRoomId());
            return existingRoom.get();
        }
        
        // 새 채팅방 생성
        String roomName = patientName + "님 채팅방";
        ChatRoom newRoom = ChatRoom.builder()
                .patientId(patientId)
                .roomName(roomName)
                .guardianId(guardianId)
                .caregiverId(caregiverId)
                .institutionId(institutionId)
                .isActive(true)
                .build();
        
        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        log.info("새 채팅방 생성: {}", savedRoom.getChatRoomId());
        
        // 참여자 추가
        addParticipant(savedRoom.getChatRoomId(), guardianId, ChatRoomParticipant.UserType.GUARDIAN);
        addParticipant(savedRoom.getChatRoomId(), caregiverId, ChatRoomParticipant.UserType.CAREGIVER);
        
        return savedRoom;
    }

    // 채팅방 참여자 추가
    @Transactional
    public ChatRoomParticipant addParticipant(Long chatRoomId, Long userId, ChatRoomParticipant.UserType userType) {
        log.info("채팅방 참여자 추가: chatRoomId={}, userId={}, userType={}", chatRoomId, userId, userType);
        
        ChatRoomParticipant participant = ChatRoomParticipant.builder()
                .chatRoom(ChatRoom.builder().chatRoomId(chatRoomId).build())
                .userId(userId)
                .userType(userType)
                .joinedAt(LocalDateTime.now())
                .build();
        
        return chatRoomParticipantRepository.save(participant);
    }

    // 메시지 전송
    @Transactional
    public ChatMessage sendMessage(Long chatRoomId, Long senderId, ChatMessage.SenderType senderType, String messageText) {
        log.info("메시지 전송: chatRoomId={}, senderId={}, senderType={}", chatRoomId, senderId, senderType);
        
        ChatMessage message = ChatMessage.builder()
                .chatRoom(ChatRoom.builder().chatRoomId(chatRoomId).build())
                .senderId(senderId)
                .senderType(senderType)
                .messageText(messageText)
                .messageType(ChatMessage.MessageType.TEXT)
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.info("메시지 저장 완료: messageId={}", savedMessage.getMessageId());
        
        // 채팅 알림 생성 (수신자에게만 알림)
        try {
            log.info("채팅 알림 생성 시작: chatRoomId={}, senderType={}", chatRoomId, senderType);
            
            // 간단한 테스트: 보호자가 메시지를 보낸 경우 요양보호사(1)에게 알림
            if (senderType == ChatMessage.SenderType.GUARDIAN) {
                log.info("보호자 메시지 감지 - 요양보호사에게 알림 생성");
                notificationService.createChatNotification(1L, com.ansimyoyang.domain.Notification.UserType.CAREGIVER, "보호자", chatRoomId);
                log.info("채팅 알림 생성 완료: recipientId=1, recipientType=CAREGIVER");
            } else if (senderType == ChatMessage.SenderType.CAREGIVER) {
                log.info("요양보호사 메시지 감지 - 보호자에게 알림 생성");
                notificationService.createChatNotification(1L, com.ansimyoyang.domain.Notification.UserType.GUARDIAN, "요양보호사", chatRoomId);
                log.info("채팅 알림 생성 완료: recipientId=1, recipientType=GUARDIAN");
            }
        } catch (Exception e) {
            log.error("채팅 알림 생성 실패: {}", e.getMessage(), e);
            // 알림 생성 실패해도 메시지 전송은 성공으로 처리
        }
        
        return savedMessage;
    }

    // 채팅방의 메시지 목록 조회 (페이징)
    public Page<ChatMessage> getChatMessages(Long chatRoomId, int page, int size) {
        log.info("채팅 메시지 조회: chatRoomId={}, page={}, size={}", chatRoomId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        return chatMessageRepository.findByChatRoomChatRoomIdOrderBySentAtAsc(chatRoomId, pageable);
    }

    // 채팅방의 최근 메시지 조회
    public List<ChatMessage> getRecentMessages(Long chatRoomId, int limit) {
        log.info("최근 메시지 조회: chatRoomId={}, limit={}", chatRoomId, limit);
        
        Pageable pageable = PageRequest.of(0, limit);
        return chatMessageRepository.findRecentMessagesByChatRoomId(chatRoomId, pageable);
    }

    // 특정 시간 이후의 메시지 조회
    public List<ChatMessage> getMessagesSince(Long chatRoomId, LocalDateTime since) {
        log.info("특정 시간 이후 메시지 조회: chatRoomId={}, since={}", chatRoomId, since);
        
        return chatMessageRepository.findMessagesSince(chatRoomId, since);
    }

    // 사용자의 채팅방 목록 조회
    public List<ChatRoom> getUserChatRooms(Long userId, String userType) {
        log.info("사용자 채팅방 목록 조회: userId={}, userType={}", userId, userType);
        
        if ("GUARDIAN".equals(userType)) {
            return chatRoomRepository.findByGuardianId(userId);
        } else if ("CAREGIVER".equals(userType)) {
            return chatRoomRepository.findByCaregiverId(userId);
        }
        
        return List.of();
    }

    // 채팅방 조회
    public Optional<ChatRoom> getChatRoom(Long chatRoomId) {
        log.info("채팅방 조회: chatRoomId={}", chatRoomId);
        
        return chatRoomRepository.findById(chatRoomId);
    }

    // 환자 ID로 채팅방 조회
    public Optional<ChatRoom> getChatRoomByPatientId(Long patientId) {
        log.info("환자 ID로 채팅방 조회: patientId={}", patientId);
        
        return chatRoomRepository.findByPatientId(patientId);
    }

    // 읽지 않은 메시지 개수 조회
    public long getUnreadMessageCount(Long chatRoomId, Long userId, ChatMessage.SenderType senderType) {
        log.info("읽지 않은 메시지 개수 조회: chatRoomId={}, userId={}, senderType={}", chatRoomId, userId, senderType);
        
        return chatMessageRepository.countUnreadMessagesBySenderType(chatRoomId, senderType);
    }

    // 메시지를 읽음 상태로 변경
    @Transactional
    public int markMessagesAsRead(Long chatRoomId, Long userId, ChatMessage.SenderType senderType) {
        log.info("메시지 읽음 처리: chatRoomId={}, userId={}, senderType={}", chatRoomId, userId, senderType);
        
        int updatedCount = chatMessageRepository.markMessagesAsReadBySenderType(chatRoomId, senderType);
        
        // 마지막 읽은 시간 업데이트
        ChatRoomParticipant.UserType userType = senderType == ChatMessage.SenderType.GUARDIAN 
            ? ChatRoomParticipant.UserType.GUARDIAN 
            : ChatRoomParticipant.UserType.CAREGIVER;
        
        chatRoomParticipantRepository.updateLastReadAt(chatRoomId, userId, userType, LocalDateTime.now());
        
        log.info("읽음 처리 완료: {}개 메시지", updatedCount);
        return updatedCount;
    }

    // 채팅방 참여자 확인
    public boolean isParticipant(Long chatRoomId, Long userId, String userType) {
        log.info("채팅방 참여자 확인: chatRoomId={}, userId={}, userType={}", chatRoomId, userId, userType);
        
        ChatRoomParticipant.UserType participantType = "GUARDIAN".equals(userType) 
            ? ChatRoomParticipant.UserType.GUARDIAN 
            : ChatRoomParticipant.UserType.CAREGIVER;
        
        return chatRoomParticipantRepository.existsByChatRoomChatRoomIdAndUserIdAndUserType(
            chatRoomId, userId, participantType);
    }

    // 채팅방 비활성화
    @Transactional
    public void deactivateChatRoom(Long chatRoomId) {
        log.info("채팅방 비활성화: chatRoomId={}", chatRoomId);
        
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isPresent()) {
            chatRoom.get().setIsActive(false);
            chatRoomRepository.save(chatRoom.get());
            log.info("채팅방 비활성화 완료");
        }
    }
}


