package com.ansimyoyang.repository.chat;

import com.ansimyoyang.domain.chat.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    // 채팅방의 참여자 목록 조회
    List<ChatRoomParticipant> findByChatRoomChatRoomId(Long chatRoomId);

    // 특정 사용자의 참여 정보 조회
    Optional<ChatRoomParticipant> findByChatRoomChatRoomIdAndUserIdAndUserType(
            Long chatRoomId, Long userId, ChatRoomParticipant.UserType userType);

    // 사용자가 참여 중인 채팅방 목록 조회
    List<ChatRoomParticipant> findByUserIdAndUserType(Long userId, ChatRoomParticipant.UserType userType);

    // 채팅방에 특정 사용자가 참여 중인지 확인
    boolean existsByChatRoomChatRoomIdAndUserIdAndUserType(
            Long chatRoomId, Long userId, ChatRoomParticipant.UserType userType);

    // 마지막 읽은 시간 업데이트
    @Modifying
    @Query("UPDATE ChatRoomParticipant crp SET crp.lastReadAt = :lastReadAt WHERE crp.chatRoom.chatRoomId = :chatRoomId AND crp.userId = :userId AND crp.userType = :userType")
    int updateLastReadAt(@Param("chatRoomId") Long chatRoomId, 
                        @Param("userId") Long userId, 
                        @Param("userType") ChatRoomParticipant.UserType userType, 
                        @Param("lastReadAt") LocalDateTime lastReadAt);

    // 채팅방의 모든 참여자 조회 (사용자 타입별)
    @Query("SELECT crp FROM ChatRoomParticipant crp WHERE crp.chatRoom.chatRoomId = :chatRoomId AND crp.userType = :userType")
    List<ChatRoomParticipant> findByChatRoomIdAndUserType(@Param("chatRoomId") Long chatRoomId, 
                                                         @Param("userType") ChatRoomParticipant.UserType userType);
}


