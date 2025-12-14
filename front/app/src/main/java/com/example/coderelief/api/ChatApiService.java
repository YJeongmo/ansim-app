package com.example.coderelief.api;

import com.example.coderelief.models.ChatMessage;
import com.example.coderelief.models.ChatRoom;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApiService {

    // 채팅방 생성 또는 조회
    @POST("api/chat/rooms")
    Call<Map<String, Object>> createOrGetChatRoom(@Body Map<String, Object> request);

    // 메시지 전송
    @POST("api/chat/messages")
    Call<Map<String, Object>> sendMessage(@Body Map<String, Object> request);

    // 채팅방의 메시지 목록 조회 (페이징)
    @GET("api/chat/rooms/{chatRoomId}/messages")
    Call<Map<String, Object>> getChatMessages(
            @Path("chatRoomId") Long chatRoomId,
            @Query("page") int page,
            @Query("size") int size);

    // 채팅방의 최근 메시지 조회
    @GET("api/chat/rooms/{chatRoomId}/recent")
    Call<Map<String, Object>> getRecentMessages(
            @Path("chatRoomId") Long chatRoomId,
            @Query("limit") int limit);

    // 특정 시간 이후의 메시지 조회
    @GET("api/chat/rooms/{chatRoomId}/messages/since")
    Call<Map<String, Object>> getMessagesSince(
            @Path("chatRoomId") Long chatRoomId,
            @Query("since") String since);

    // 사용자의 채팅방 목록 조회
    @GET("api/chat/users/{userId}/rooms")
    Call<Map<String, Object>> getUserChatRooms(
            @Path("userId") Long userId,
            @Query("userType") String userType);

    // 채팅방 조회
    @GET("api/chat/rooms/{chatRoomId}")
    Call<Map<String, Object>> getChatRoom(@Path("chatRoomId") Long chatRoomId);

    // 환자 ID로 채팅방 조회
    @GET("api/chat/rooms/patient/{patientId}")
    Call<Map<String, Object>> getChatRoomByPatientId(@Path("patientId") Long patientId);

    // 읽지 않은 메시지 개수 조회
    @GET("api/chat/rooms/{chatRoomId}/unread-count")
    Call<Map<String, Object>> getUnreadMessageCount(
            @Path("chatRoomId") Long chatRoomId,
            @Query("userId") Long userId,
            @Query("senderType") String senderType);

    // 메시지를 읽음 상태로 변경
    @PUT("api/chat/rooms/{chatRoomId}/mark-read")
    Call<Map<String, Object>> markMessagesAsRead(
            @Path("chatRoomId") Long chatRoomId,
            @Query("userId") Long userId,
            @Query("senderType") String senderType);
}


