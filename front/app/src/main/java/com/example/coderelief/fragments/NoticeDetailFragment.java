package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeDetailFragment extends Fragment {
    
    private TextView tvTitle, tvContent, tvDate, tvAuthor, tvPriority;
    private Button btnBack;
    private Button btnEdit;
    private Button btnDelete;
    
    private long noticeId;
    private String userRole;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        android.util.Log.d("NoticeDetail", "onCreateView 호출됨 - 간단한 레이아웃 사용");
        return inflater.inflate(R.layout.fragment_notice_detail_simple, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        android.util.Log.d("NoticeDetail", "onViewCreated 호출됨");
        
        initViews(view);
        parseArguments();
        setupClickListeners();
        loadNoticeDetail();
    }
    
    private void initViews(View view) {
        android.util.Log.d("NoticeDetail", "initViews 호출됨");
        
        tvTitle = view.findViewById(R.id.tv_notice_detail_title);
        tvContent = view.findViewById(R.id.tv_notice_detail_content);
        tvDate = view.findViewById(R.id.tv_notice_detail_date);
        tvAuthor = view.findViewById(R.id.tv_notice_detail_author);
        tvPriority = view.findViewById(R.id.tv_notice_detail_priority);
        btnBack = view.findViewById(R.id.btn_back);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
        
        android.util.Log.d("NoticeDetail", "Views 초기화 완료 - tvTitle: " + (tvTitle != null ? "OK" : "NULL"));
    }
    
    private void parseArguments() {
        Bundle args = getArguments();
        if (args != null) {
            noticeId = args.getLong("notice_id", 0);
            userRole = args.getString("user_role", "guardian");
            android.util.Log.d("NoticeDetail", "Arguments 파싱 완료 - noticeId: " + noticeId + ", userRole: " + userRole);
        } else {
            android.util.Log.e("NoticeDetail", "Arguments가 null입니다!");
        }
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        
        // 수정 버튼 클릭 리스너
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                // 공지사항 수정 화면으로 이동
                editNotice();
            });
        }
        
        // 삭제 버튼 클릭 리스너
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                // 삭제 확인 다이얼로그 표시
                showDeleteConfirmDialog();
            });
        }
    }
    
    private void loadNoticeDetail() {
        if (noticeId <= 0) {
            tvTitle.setText("공지사항을 찾을 수 없습니다");
            tvContent.setText("요청하신 공지사항을 찾을 수 없습니다.");
            return;
        }
        
        // Show loading state
        tvTitle.setText("로딩 중...");
        tvContent.setText("공지사항을 불러오는 중입니다...");
        tvDate.setText("");
        tvAuthor.setText("");
        tvPriority.setText("");
        
        // Call API to get notice detail
        ApiService apiService = ApiClient.getApiService();
        Call<Map<String, Object>> call = apiService.getNoticeById(noticeId);
        
        // Debug log
        android.util.Log.d("NoticeDetail", "API 호출 시작: noticeId=" + noticeId);
        
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                android.util.Log.d("NoticeDetail", "API 응답 받음: success=" + response.isSuccessful() + ", code=" + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    android.util.Log.d("NoticeDetail", "응답 데이터: " + responseBody.toString());
                    
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        // Success - display notice data
                        Map<String, Object> noticeData = (Map<String, Object>) responseBody.get("data");
                        if (noticeData != null) {
                            android.util.Log.d("NoticeDetail", "공지사항 데이터 파싱 성공");
                            displayNoticeData(noticeData);
                        } else {
                            android.util.Log.e("NoticeDetail", "공지사항 데이터가 null입니다");
                            showError("공지사항 데이터를 찾을 수 없습니다.");
                        }
                    } else {
                        // API returned error
                        String message = (String) responseBody.get("message");
                        android.util.Log.e("NoticeDetail", "API 에러: " + message);
                        showError("공지사항을 불러올 수 없습니다: " + (message != null ? message : "알 수 없는 오류"));
                    }
                } else {
                    // HTTP error
                    android.util.Log.e("NoticeDetail", "HTTP 에러: " + response.code());
                    showError("공지사항을 불러올 수 없습니다: 서버 오류 (HTTP " + response.code() + ")");
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Network error
                android.util.Log.e("NoticeDetail", "네트워크 에러: " + t.getMessage(), t);
                showError("공지사항을 불러올 수 없습니다: " + t.getMessage());
            }
        });
    }
    
    private void displayTestData() {
        android.util.Log.d("NoticeDetail", "displayTestData 호출됨");
        
        // 임시 테스트 데이터 표시
        if (tvTitle != null) {
            tvTitle.setText("qqq");
            android.util.Log.d("NoticeDetail", "제목 설정 완료");
        } else {
            android.util.Log.e("NoticeDetail", "tvTitle이 null입니다!");
        }
        
        if (tvContent != null) {
            tvContent.setText("www");
            android.util.Log.d("NoticeDetail", "내용 설정 완료");
        } else {
            android.util.Log.e("NoticeDetail", "tvContent가 null입니다!");
        }
        
        if (tvDate != null) {
            tvDate.setText("2025년 09월 02일 16:02");
        }
        
        if (tvAuthor != null) {
            tvAuthor.setText("작성자: 박요양사");
        }
        
        if (tvPriority != null) {
            tvPriority.setText("일반");
            tvPriority.setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
        }
        
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "공지사항 상세보기 (ID: " + noticeId + ") - 테스트 데이터", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
        
        android.util.Log.d("NoticeDetail", "테스트 데이터 표시 완료");
    }
    
    private void displayNoticeData(Map<String, Object> noticeData) {
        android.util.Log.d("NoticeDetail", "displayNoticeData 호출됨");
        
        // Set title
        String title = (String) noticeData.get("title");
        if (tvTitle != null) {
            tvTitle.setText(title != null ? title : "제목 없음");
            android.util.Log.d("NoticeDetail", "제목 설정: " + title);
        }
        
        // Set content
        String content = (String) noticeData.get("content");
        if (tvContent != null) {
            tvContent.setText(content != null ? content : "내용 없음");
            android.util.Log.d("NoticeDetail", "내용 설정: " + content);
        }
        
        // Set date
        String createdAt = (String) noticeData.get("createdAt");
        if (tvDate != null) {
            if (createdAt != null) {
                try {
                    // Parse ISO 8601 format and format for display
                    java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                    java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", java.util.Locale.getDefault());
                    java.util.Date date = inputFormat.parse(createdAt);
                    tvDate.setText(outputFormat.format(date));
                } catch (Exception e) {
                    tvDate.setText(createdAt);
                }
            } else {
                tvDate.setText("");
            }
        }
        
        // Set author (caregiver name)
        if (tvAuthor != null) {
            Map<String, Object> caregiver = (Map<String, Object>) noticeData.get("caregiver");
            if (caregiver != null) {
                String caregiverName = (String) caregiver.get("name");
                tvAuthor.setText("작성자: " + (caregiverName != null ? caregiverName : "알 수 없음"));
            } else {
                tvAuthor.setText("작성자: 알 수 없음");
            }
        }
        
        // Set priority
        if (tvPriority != null) {
            String priority = (String) noticeData.get("priority");
            if (priority != null) {
                switch (priority) {
                    case "URGENT":
                        tvPriority.setText("긴급");
                        tvPriority.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_red_light));
                        break;
                    case "IMPORTANT":
                        tvPriority.setText("중요");
                        tvPriority.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_orange_light));
                        break;
                    default:
                        tvPriority.setText("일반");
                        tvPriority.setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
                        break;
                }
            } else {
                tvPriority.setText("일반");
                tvPriority.setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
            }
        }
        
        android.util.Log.d("NoticeDetail", "공지사항 데이터 표시 완료");
        
        // 사용자 역할에 따라 수정/삭제 버튼 표시
        updateButtonVisibility();
    }
    
    /**
     * 사용자 역할에 따라 수정/삭제 버튼 표시 여부 결정
     */
    private void updateButtonVisibility() {
        if (btnEdit != null && btnDelete != null) {
            // 요양보호사(caregiver)인 경우에만 수정/삭제 버튼 표시
            if ("caregiver".equals(userRole)) {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                android.util.Log.d("NoticeDetail", "요양보호사 권한 - 수정/삭제 버튼 표시");
            } else {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                android.util.Log.d("NoticeDetail", "보호자 권한 - 수정/삭제 버튼 숨김");
            }
        }
    }
    
    private void showError(String message) {
        tvTitle.setText("오류");
        tvContent.setText(message);
        tvDate.setText("");
        tvAuthor.setText("");
        tvPriority.setText("");
        
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 공지사항 수정 화면으로 이동
     */
    private void editNotice() {
        // 현재 공지사항 데이터를 수정 화면으로 전달
        EditNoticeFragment fragment = new EditNoticeFragment();
        Bundle args = new Bundle();
        args.putString("user_role", userRole);
        args.putLong("notice_id", noticeId);
        
        // 현재 표시된 데이터를 전달
        if (tvTitle != null) {
            args.putString("original_title", tvTitle.getText().toString());
        }
        if (tvContent != null) {
            args.putString("original_content", tvContent.getText().toString());
        }
        
        // 우선순위 정보 전달 (현재 표시된 텍스트를 기반으로)
        String priority = "NORMAL";
        if (tvPriority != null) {
            String priorityText = tvPriority.getText().toString();
            if ("긴급".equals(priorityText)) {
                priority = "URGENT";
            } else if ("중요".equals(priorityText)) {
                priority = "IMPORTANT";
            }
        }
        args.putString("original_priority", priority);
        
        fragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    /**
     * 삭제 확인 다이얼로그 표시
     */
    private void showDeleteConfirmDialog() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("공지사항 삭제")
                .setMessage("정말로 이 공지사항을 삭제하시겠습니까?\n삭제된 공지사항은 복구할 수 없습니다.")
                .setPositiveButton("삭제", (dialog, which) -> {
                    deleteNotice();
                })
                .setNegativeButton("취소", null)
                .show();
    }
    
    /**
     * 공지사항 목록으로 이동
     */
    private void navigateToNoticeList() {
        NoticeListFragment fragment = new NoticeListFragment();
        Bundle args = new Bundle();
        args.putString("user_role", userRole);
        fragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    /**
     * 공지사항 삭제 API 호출
     */
    private void deleteNotice() {
        if (noticeId <= 0) {
            android.widget.Toast.makeText(getContext(), "삭제할 공지사항을 찾을 수 없습니다", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 로딩 표시
        android.widget.Toast.makeText(getContext(), "삭제 중...", android.widget.Toast.LENGTH_SHORT).show();
        
        // API 호출
        ApiService apiService = ApiClient.getApiService();
        Call<java.util.Map<String, Object>> call = apiService.deleteNotice(noticeId);
        
        call.enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call, Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        // 삭제 성공
                        String message = (String) responseBody.get("message");
                        android.widget.Toast.makeText(getContext(), 
                            message != null ? message : "공지사항이 삭제되었습니다", 
                            android.widget.Toast.LENGTH_SHORT).show();
                        
                        // 공지사항 목록으로 돌아가기
                        navigateToNoticeList();
                    } else {
                        // API 에러
                        String message = (String) responseBody.get("message");
                        android.widget.Toast.makeText(getContext(), 
                            "삭제 실패: " + (message != null ? message : "알 수 없는 오류"), 
                            android.widget.Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // HTTP 에러
                    android.widget.Toast.makeText(getContext(), 
                        "삭제 실패: 서버 오류 (HTTP " + response.code() + ")", 
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                // 네트워크 에러
                android.widget.Toast.makeText(getContext(), 
                    "삭제 실패: " + t.getMessage(), 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}