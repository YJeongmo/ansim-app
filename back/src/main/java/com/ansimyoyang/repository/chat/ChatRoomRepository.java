package com.ansimyoyang.repository.chat;

import com.ansimyoyang.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 환자 ID로 채팅방 조회
    Optional<ChatRoom> findByPatientId(Long patientId);

    // 보호자 ID로 참여 중인 채팅방 목록 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.guardianId = :guardianId AND cr.isActive = true")
    List<ChatRoom> findByGuardianId(@Param("guardianId") Long guardianId);

    // 요양보호사 ID로 참여 중인 채팅방 목록 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.caregiverId = :caregiverId AND cr.isActive = true")
    List<ChatRoom> findByCaregiverId(@Param("caregiverId") Long caregiverId);

    // 요양원 ID로 채팅방 목록 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.institutionId = :institutionId AND cr.isActive = true")
    List<ChatRoom> findByInstitutionId(@Param("institutionId") Long institutionId);

    // 사용자가 참여 중인 채팅방 조회 (보호자 또는 요양보호사)
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "(cr.guardianId = :userId OR cr.caregiverId = :userId) AND cr.isActive = true")
    List<ChatRoom> findByUserId(@Param("userId") Long userId);

    // 특정 환자의 채팅방이 존재하는지 확인
    boolean existsByPatientId(Long patientId);

    // 활성화된 채팅방 개수 조회
    long countByIsActiveTrue();
}


