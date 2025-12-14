package com.ansimyoyang.controller;

import com.ansimyoyang.domain.chat.ChatMessage;
import com.ansimyoyang.domain.chat.ChatRoom;
import com.ansimyoyang.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성 또는 조회
    @PostMapping("/rooms")
    public ResponseEntity<Map<String, Object>> getOrCreateChatRoom(@RequestBody Map<String, Object> request) {
        try {
            log.info("채팅방 생성/조회 요청: {}", request);
            
            // chatRoomId가 있으면 직접 조회, 없으면 생성/조회
            if (request.containsKey("chatRoomId") && request.get("chatRoomId") != null) {
                Long chatRoomId = Long.valueOf(request.get("chatRoomId").toString());
                log.info("채팅방 ID로 직접 조회: chatRoomId={}", chatRoomId);
                
                Optional<ChatRoom> chatRoom = chatService.getChatRoom(chatRoomId);
                if (chatRoom.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("chatRoomId", chatRoom.get().getChatRoomId());
                    response.put("roomName", chatRoom.get().getRoomName());
                    response.put("patientId", chatRoom.get().getPatientId());
                    response.put("guardianId", chatRoom.get().getGuardianId());
                    response.put("caregiverId", chatRoom.get().getCaregiverId());
                    response.put("institutionId", chatRoom.get().getInstitutionId());
                    
                    log.info("채팅방 ID로 조회 성공: {}", chatRoomId);
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "채팅방을 찾을 수 없습니다.");
                    return ResponseEntity.notFound().build();
                }
            }
            
            // 기존 로직: patientId로 채팅방 생성/조회
            Long patientId = Long.valueOf(request.get("patientId").toString());
            String patientName = request.get("patientName").toString();
            Long guardianId = Long.valueOf(request.get("guardianId").toString());
            Long caregiverId = Long.valueOf(request.get("caregiverId").toString());
            Long institutionId = Long.valueOf(request.get("institutionId").toString());
            
            ChatRoom chatRoom = chatService.getOrCreateChatRoom(patientId, patientName, guardianId, caregiverId, institutionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("chatRoomId", chatRoom.getChatRoomId());
            response.put("roomName", chatRoom.getRoomName());
            response.put("patientId", chatRoom.getPatientId());
            response.put("guardianId", chatRoom.getGuardianId());
            response.put("caregiverId", chatRoom.getCaregiverId());
            response.put("institutionId", chatRoom.getInstitutionId());
            
            log.info("채팅방 생성/조회 성공: {}", chatRoom.getChatRoomId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("채팅방 생성/조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "채팅방 생성/조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 메시지 전송
    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            log.info("메시지 전송 요청: {}", request);
            
            Long chatRoomId = Long.valueOf(request.get("chatRoomId").toString());
            Long senderId = Long.valueOf(request.get("senderId").toString());
            String senderType = request.get("senderType").toString();
            String messageText = request.get("messageText").toString();
            
            ChatMessage.SenderType senderTypeEnum = ChatMessage.SenderType.valueOf(senderType);
            ChatMessage message = chatService.sendMessage(chatRoomId, senderId, senderTypeEnum, messageText);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messageId", message.getMessageId());
            response.put("messageText", message.getMessageText());
            response.put("senderId", message.getSenderId());
            response.put("senderType", message.getSenderType());
            response.put("sentAt", message.getSentAt());
            
            log.info("메시지 전송 성공: messageId={}", message.getMessageId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "메시지 전송에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 채팅방의 메시지 목록 조회 (페이징)
    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<Map<String, Object>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("채팅 메시지 조회: chatRoomId={}, page={}, size={}", chatRoomId, page, size);
            
            Page<ChatMessage> messagePage = chatService.getChatMessages(chatRoomId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messagePage.getContent());
            response.put("totalPages", messagePage.getTotalPages());
            response.put("totalElements", messagePage.getTotalElements());
            response.put("currentPage", page);
            response.put("size", size);
            
            log.info("채팅 메시지 조회 성공: {}개 메시지", messagePage.getTotalElements());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("채팅 메시지 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "메시지 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 채팅방의 최근 메시지 조회
    @GetMapping("/rooms/{chatRoomId}/recent")
    public ResponseEntity<Map<String, Object>> getRecentMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("최근 메시지 조회: chatRoomId={}, limit={}", chatRoomId, limit);
            
            List<ChatMessage> messages = chatService.getRecentMessages(chatRoomId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messages);
            response.put("count", messages.size());
            
            log.info("최근 메시지 조회 성공: {}개 메시지", messages.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("최근 메시지 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "최근 메시지 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 특정 시간 이후의 메시지 조회
    @GetMapping("/rooms/{chatRoomId}/messages/since")
    public ResponseEntity<Map<String, Object>> getMessagesSince(
            @PathVariable Long chatRoomId,
            @RequestParam String since) {
        try {
            log.info("특정 시간 이후 메시지 조회: chatRoomId={}, since={}", chatRoomId, since);
            
            LocalDateTime sinceTime = LocalDateTime.parse(since);
            List<ChatMessage> messages = chatService.getMessagesSince(chatRoomId, sinceTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messages);
            response.put("count", messages.size());
            
            log.info("특정 시간 이후 메시지 조회 성공: {}개 메시지", messages.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("특정 시간 이후 메시지 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "메시지 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 사용자의 채팅방 목록 조회
    @GetMapping("/users/{userId}/rooms")
    public ResponseEntity<Map<String, Object>> getUserChatRooms(
            @PathVariable Long userId,
            @RequestParam String userType) {
        try {
            log.info("사용자 채팅방 목록 조회: userId={}, userType={}", userId, userType);
            
            List<ChatRoom> chatRooms = chatService.getUserChatRooms(userId, userType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("chatRooms", chatRooms);
            response.put("count", chatRooms.size());
            
            log.info("사용자 채팅방 목록 조회 성공: {}개 채팅방", chatRooms.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("사용자 채팅방 목록 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "채팅방 목록 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 채팅방 조회
    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<Map<String, Object>> getChatRoom(@PathVariable Long chatRoomId) {
        try {
            log.info("채팅방 조회: chatRoomId={}", chatRoomId);
            
            Optional<ChatRoom> chatRoom = chatService.getChatRoom(chatRoomId);
            
            if (chatRoom.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("chatRoom", chatRoom.get());
                
                log.info("채팅방 조회 성공: {}", chatRoomId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "채팅방을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("채팅방 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "채팅방 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 환자 ID로 채팅방 조회
    @GetMapping("/rooms/patient/{patientId}")
    public ResponseEntity<Map<String, Object>> getChatRoomByPatientId(@PathVariable Long patientId) {
        try {
            log.info("환자 ID로 채팅방 조회: patientId={}", patientId);
            
            Optional<ChatRoom> chatRoom = chatService.getChatRoomByPatientId(patientId);
            
            if (chatRoom.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("chatRoom", chatRoom.get());
                
                log.info("환자 ID로 채팅방 조회 성공: {}", patientId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "해당 환자의 채팅방을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("환자 ID로 채팅방 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "채팅방 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 읽지 않은 메시지 개수 조회
    @GetMapping("/rooms/{chatRoomId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadMessageCount(
            @PathVariable Long chatRoomId,
            @RequestParam Long userId,
            @RequestParam String senderType) {
        try {
            log.info("읽지 않은 메시지 개수 조회: chatRoomId={}, userId={}, senderType={}", chatRoomId, userId, senderType);
            
            ChatMessage.SenderType senderTypeEnum = ChatMessage.SenderType.valueOf(senderType);
            long unreadCount = chatService.getUnreadMessageCount(chatRoomId, userId, senderTypeEnum);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("unreadCount", unreadCount);
            
            log.info("읽지 않은 메시지 개수 조회 성공: {}개", unreadCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("읽지 않은 메시지 개수 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "읽지 않은 메시지 개수 조회에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 메시지를 읽음 상태로 변경
    @PutMapping("/rooms/{chatRoomId}/mark-read")
    public ResponseEntity<Map<String, Object>> markMessagesAsRead(
            @PathVariable Long chatRoomId,
            @RequestParam Long userId,
            @RequestParam String senderType) {
        try {
            log.info("메시지 읽음 처리: chatRoomId={}, userId={}, senderType={}", chatRoomId, userId, senderType);
            
            ChatMessage.SenderType senderTypeEnum = ChatMessage.SenderType.valueOf(senderType);
            int updatedCount = chatService.markMessagesAsRead(chatRoomId, userId, senderTypeEnum);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("updatedCount", updatedCount);
            response.put("message", updatedCount + "개의 메시지를 읽음 처리했습니다.");
            
            log.info("메시지 읽음 처리 성공: {}개 메시지", updatedCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("메시지 읽음 처리 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "메시지 읽음 처리에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}


