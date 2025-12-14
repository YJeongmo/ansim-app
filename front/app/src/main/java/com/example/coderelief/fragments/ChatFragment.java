package com.example.coderelief.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.adapters.ChatMessageAdapter;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ChatApiService;
import com.example.coderelief.models.ChatMessage;
import com.example.coderelief.models.ChatRoom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    
    private ImageView ivChatUserAvatar, ivChatMenu;
    private TextView tvChatUserName, tvChatStatus;
    private RecyclerView rvChatMessages;
    private LinearLayout layoutTypingIndicator;
    private ImageButton btnAttachment;
    private TextInputEditText etMessageInput;
    private FloatingActionButton fabSendMessage;
    
    private String chatType; // "guardian", "caregiver", "care_center"
    private long patientId;
    private int chatId;
    private String familyName;
    private String roomNumber;
    
    // 채팅 관련 변수들
    private ChatMessageAdapter messageAdapter;
    private ChatApiService chatApiService;
    private Long currentUserId;
    private String currentUserType;
    private Long chatRoomId;
    private List<ChatMessage> messages;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        initializeChat(); // setupRecyclerView() 이전에 호출
        setupChatInfo();
        setupClickListeners();
        setupMessageInput();
        setupRecyclerView();
        
        // 채팅방 생성/조회 및 메시지 로드
        createOrGetChatRoom();
    }
    
    private void initViews(View view) {
        ivChatUserAvatar = view.findViewById(R.id.iv_chat_user_avatar);
        ivChatMenu = view.findViewById(R.id.iv_chat_menu);
        tvChatUserName = view.findViewById(R.id.tv_chat_user_name);
        tvChatStatus = view.findViewById(R.id.tv_chat_status);
        rvChatMessages = view.findViewById(R.id.rv_chat_messages);
        layoutTypingIndicator = view.findViewById(R.id.layout_typing_indicator);
        btnAttachment = view.findViewById(R.id.btn_attachment);
        etMessageInput = view.findViewById(R.id.et_message_input);
        fabSendMessage = view.findViewById(R.id.fab_send_message);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            chatType = args.getString("chat_type", "guardian");
            patientId = args.getLong("patient_id", -1L);
            chatId = args.getInt("chat_id", -1);
            familyName = args.getString("family_name", "");
            roomNumber = args.getString("room_number", "");
            
            // 알림에서 온 경우 chat_room_id 사용
            if (args.containsKey("chat_room_id")) {
                chatRoomId = args.getLong("chat_room_id");
            }
            
            // 사용자 정보는 initializeChat()에서 처리하므로 여기서는 제거
        } else {
            chatType = "guardian";
            patientId = -1L;
            chatId = -1;
            familyName = "";
            roomNumber = "";
        }
    }
    
    private void setupChatInfo() {
        // 현재 사용자 타입에 따라 채팅 상대방 정보 표시
        if ("GUARDIAN".equals(currentUserType)) {
            // 보호자로 로그인한 경우 -> 상대방은 요양보호사
            tvChatUserName.setText("요양보호사");
            tvChatStatus.setText("온라인");
        } else if ("CAREGIVER".equals(currentUserType)) {
            // 요양보호사로 로그인한 경우 -> 상대방은 보호자
            tvChatUserName.setText("보호자");
            tvChatStatus.setText("온라인");
        } else {
            // 기본값
            tvChatUserName.setText("채팅");
            tvChatStatus.setText("온라인");
        }
        
        // TODO: Load actual user avatar based on family/care center
        // Glide.with(this).load(avatarUrl).into(ivChatUserAvatar);
    }
    
    private void setupClickListeners() {
        fabSendMessage.setOnClickListener(v -> sendMessage());
        
        btnAttachment.setOnClickListener(v -> {
            // TODO: Show attachment options (photo, file, etc.)
            showAttachmentOptions();
        });
        
        ivChatMenu.setOnClickListener(v -> {
            // TODO: Show chat menu options
            showChatMenu();
        });
    }
    
    private void setupMessageInput() {
        etMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide send button based on text input
                boolean hasText = s.toString().trim().length() > 0;
                fabSendMessage.setVisibility(hasText ? View.VISIBLE : View.GONE);
                
                // TODO: Send typing indicator to other party
                if (hasText) {
                    sendTypingIndicator();
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        etMessageInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });
    }
    
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Start from bottom
        rvChatMessages.setLayoutManager(layoutManager);
        
        // 메시지 어댑터 설정
        messages = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(currentUserType, currentUserId);
        rvChatMessages.setAdapter(messageAdapter);
    }
    
    private void initializeChat() {
        chatApiService = ApiClient.getChatApiService();
        messages = new ArrayList<>();
        
        // 현재 사용자 정보 설정
        Bundle args = getArguments();
        if (args != null) {
            // chat_type으로 사용자 역할 확인 (알림에서 온 경우)
            String chatType = args.getString("chat_type", "");
            
            if ("caregiver".equals(chatType)) {
                // 요양보호사로 로그인한 경우
                currentUserType = "CAREGIVER";
                currentUserId = args.getLong("caregiver_id", 1L);
            } else if ("guardian".equals(chatType)) {
                // 보호자로 로그인한 경우
                currentUserType = "GUARDIAN";
                currentUserId = args.getLong("guardian_id", 1L);
            } else {
                // 기존 user_role 방식 (하위 호환성)
                String userRole = args.getString("user_role", "guardian");
                currentUserType = "guardian".equals(userRole) ? "GUARDIAN" : "CAREGIVER";
                
                // 사용자 ID 설정
                if ("GUARDIAN".equals(currentUserType)) {
                    currentUserId = args.getLong("guardian_id", 1L);
                } else {
                    currentUserId = args.getLong("caregiver_id", 1L);
                }
            }
        } else {
            // 기본값
            currentUserType = "GUARDIAN";
            currentUserId = 1L;
        }
        
        android.util.Log.d("ChatFragment", "사용자 타입: " + currentUserType + ", 사용자 ID: " + currentUserId);
    }
    
    
    private void createOrGetChatRoom() {
        if (chatApiService == null) {
            android.widget.Toast.makeText(getContext(), "채팅 서비스를 초기화할 수 없습니다.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 채팅방 생성/조회 요청 데이터
        Map<String, Object> request = new HashMap<>();
        
        // chatRoomId가 있으면 직접 조회
        if (chatRoomId != null && chatRoomId > 0) {
            request.put("chatRoomId", chatRoomId);
            android.util.Log.d("ChatFragment", "채팅방 ID로 직접 조회: " + chatRoomId);
        } else {
            // 기존 로직: patientId로 채팅방 생성/조회
            request.put("patientId", patientId);
            request.put("patientName", familyName);
            request.put("guardianId", 1L); // 임시로 1L 사용
            request.put("caregiverId", 1L); // 임시로 1L 사용
            request.put("institutionId", 1L); // 임시로 1L 사용
        }
        
        chatApiService.createOrGetChatRoom(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        chatRoomId = ((Double) responseBody.get("chatRoomId")).longValue();
                        loadChatMessages();
                    } else {
                        String message = (String) responseBody.get("message");
                        android.widget.Toast.makeText(getContext(), 
                            "채팅방 생성 실패: " + message, 
                            android.widget.Toast.LENGTH_SHORT).show();
                    }
                } else {
                    android.widget.Toast.makeText(getContext(), 
                        "채팅방 생성 실패", 
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                android.widget.Toast.makeText(getContext(), 
                    "네트워크 오류: " + t.getMessage(), 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadChatMessages() {
        if (chatRoomId == null || chatApiService == null) {
            return;
        }
        
        chatApiService.getRecentMessages(chatRoomId, 20).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        List<Map<String, Object>> messagesData = (List<Map<String, Object>>) responseBody.get("messages");
                        if (messagesData != null) {
                            List<ChatMessage> chatMessages = parseMessages(messagesData);

                            // 시간 오름차순 정렬(과거 → 최신)
                            try {
                                Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                                    @Override
                                    public int compare(ChatMessage a, ChatMessage b) {
                                        String sa = a.getSentAt();
                                        String sb = b.getSentAt();
                                        if (sa == null && sb == null) return 0;
                                        if (sa == null) return -1;
                                        if (sb == null) return 1;
                                        // ISO-8601 문자열은 사전식 비교가 시간 순서와 일치
                                        return sa.compareTo(sb);
                                    }
                                });
                            } catch (Exception ignored) {}

                            messageAdapter.updateMessages(chatMessages);
                            
                            // 스크롤을 맨 아래로
                            if (chatMessages.size() > 0) {
                                rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                            }
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                android.widget.Toast.makeText(getContext(), 
                    "메시지 로드 실패: " + t.getMessage(), 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private List<ChatMessage> parseMessages(List<Map<String, Object>> messagesData) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        Gson gson = new Gson();
        
        for (Map<String, Object> messageData : messagesData) {
            ChatMessage message = gson.fromJson(gson.toJson(messageData), ChatMessage.class);
            chatMessages.add(message);
        }
        
        return chatMessages;
    }
    
    private void sendMessage() {
        String messageText = etMessageInput.getText() != null ? etMessageInput.getText().toString().trim() : "";
        if (messageText.isEmpty() || chatRoomId == null || chatApiService == null) {
            return;
        }
        
        // 입력 필드 초기화
        etMessageInput.setText("");
        fabSendMessage.setVisibility(View.GONE);
        
        // 메시지 전송 요청 데이터
        Map<String, Object> request = new HashMap<>();
        request.put("chatRoomId", chatRoomId);
        request.put("senderId", currentUserId);
        request.put("senderType", currentUserType);
        request.put("messageText", messageText);
        
        chatApiService.sendMessage(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        // 전송된 메시지를 UI에 추가
                        ChatMessage sentMessage = new ChatMessage();
                        sentMessage.setMessageId(((Double) responseBody.get("messageId")).longValue());
                        sentMessage.setChatRoomId(chatRoomId);
                        sentMessage.setSenderId(currentUserId);
                        sentMessage.setSenderType(currentUserType);
                        sentMessage.setMessageText(messageText);
                        sentMessage.setMessageType("TEXT");
                        sentMessage.setIsRead(false);
                        sentMessage.setSentAt(responseBody.get("sentAt").toString());
                        
                        messageAdapter.addMessage(sentMessage);
                        
                        // 스크롤을 맨 아래로
                        rvChatMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                        
                    } else {
                        String message = (String) responseBody.get("message");
                        android.widget.Toast.makeText(getContext(), 
                            "메시지 전송 실패: " + message, 
                            android.widget.Toast.LENGTH_SHORT).show();
                    }
                } else {
                    android.widget.Toast.makeText(getContext(), 
                        "메시지 전송 실패", 
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                android.widget.Toast.makeText(getContext(), 
                    "네트워크 오류: " + t.getMessage(), 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    private void sendTypingIndicator() {
        // TODO: Send typing indicator to other party via real-time messaging
        // This should notify the other user that current user is typing
    }
    
    private void showTypingIndicator(boolean show) {
        layoutTypingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            // Scroll to show typing indicator
            rvChatMessages.scrollToPosition(rvChatMessages.getAdapter() != null ? 
                rvChatMessages.getAdapter().getItemCount() - 1 : 0);
        }
    }
    
    private void showAttachmentOptions() {
        // TODO: Show bottom sheet or dialog with attachment options
        // Options: Camera, Gallery, Files, etc.
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "첨부파일 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showChatMenu() {
        // TODO: Show popup menu with chat options
        // Options: Clear chat, Block user, Report, etc.
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "채팅 메뉴 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupRealtimeMessaging() {
        // TODO: Setup WebSocket or Firebase for real-time messaging
        // This should handle:
        // - Receiving new messages
        // - Typing indicators
        // - Message delivery status
        // - Online/offline status
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Mark messages as read
        // TODO: Update message read status
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Stop typing indicator
        // TODO: Clear typing status
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cleanup real-time messaging connections
        // TODO: Disconnect WebSocket/Firebase listeners
    }
}