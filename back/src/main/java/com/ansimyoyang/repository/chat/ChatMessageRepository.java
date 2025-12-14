package com.ansimyoyang.repository.chat;

import com.ansimyoyang.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 채팅방의 메시지 목록 조회 (페이징)
    Page<ChatMessage> findByChatRoomChatRoomIdOrderBySentAtAsc(Long chatRoomId, Pageable pageable);

    // 채팅방의 최근 메시지 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId ORDER BY cm.sentAt ASC")
    List<ChatMessage> findRecentMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    // 특정 시간 이후의 메시지 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.sentAt > :since ORDER BY cm.sentAt ASC")
    List<ChatMessage> findMessagesSince(@Param("chatRoomId") Long chatRoomId, @Param("since") LocalDateTime since);

    // 읽지 않은 메시지 개수 조회
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.isRead = false AND cm.senderId != :userId")
    long countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    // 특정 사용자의 읽지 않은 메시지 개수 조회
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.isRead = false AND cm.senderType != :senderType")
    long countUnreadMessagesBySenderType(@Param("chatRoomId") Long chatRoomId, @Param("senderType") ChatMessage.SenderType senderType);

    // 메시지를 읽음 상태로 변경
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.senderId != :userId AND cm.isRead = false")
    int markMessagesAsRead(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    // 특정 발신자 타입의 메시지를 읽음 상태로 변경
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.senderType != :senderType AND cm.isRead = false")
    int markMessagesAsReadBySenderType(@Param("chatRoomId") Long chatRoomId, @Param("senderType") ChatMessage.SenderType senderType);

    // 채팅방의 마지막 메시지 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId ORDER BY cm.sentAt DESC LIMIT 1")
    ChatMessage findLastMessageByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 특정 사용자가 보낸 메시지 목록 조회
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.senderId = :senderId ORDER BY cm.sentAt DESC")
    List<ChatMessage> findByChatRoomIdAndSenderId(@Param("chatRoomId") Long chatRoomId, @Param("senderId") Long senderId);
}


