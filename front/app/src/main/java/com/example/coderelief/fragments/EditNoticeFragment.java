package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNoticeFragment extends Fragment {
    
    private EditText etTitle;
    private EditText etContent;
    private Spinner spinnerPriority;
    private Button btnSave;
    private Button btnCancel;
    
    private long noticeId;
    private String userRole;
    private String originalTitle;
    private String originalContent;
    private String originalPriority;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_notice, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        parseArguments();
        setupClickListeners();
        loadNoticeData();
    }
    
    private void initViews(View view) {
        etTitle = view.findViewById(R.id.et_notice_title);
        etContent = view.findViewById(R.id.et_notice_content);
        spinnerPriority = view.findViewById(R.id.spinner_priority);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
    }
    
    private void parseArguments() {
        Bundle args = getArguments();
        if (args != null) {
            noticeId = args.getLong("notice_id", 0);
            userRole = args.getString("user_role", "caregiver");
            originalTitle = args.getString("original_title", "");
            originalContent = args.getString("original_content", "");
            originalPriority = args.getString("original_priority", "NORMAL");
        }
    }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            saveNotice();
        });
        
        btnCancel.setOnClickListener(v -> {
            // 수정 취소 - 상세보기로 돌아가기 (백스택에서 현재 Fragment 제거)
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
    
    private void loadNoticeData() {
        // 기존 데이터로 폼 채우기
        etTitle.setText(originalTitle);
        etContent.setText(originalContent);
        
        // 우선순위 스피너 설정
        String[] priorityLabels = {"일반", "중요", "긴급"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, priorityLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
        
        // 기존 우선순위로 선택
        String[] priorities = {"NORMAL", "IMPORTANT", "URGENT"};
        for (int i = 0; i < priorities.length; i++) {
            if (priorities[i].equals(originalPriority)) {
                spinnerPriority.setSelection(i);
                break;
            }
        }
    }
    
    private void saveNotice() {
        // 입력값 검증
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 우선순위 가져오기
        String[] priorities = {"NORMAL", "IMPORTANT", "URGENT"};
        String priority = priorities[spinnerPriority.getSelectedItemPosition()];
        
        // API 호출을 위한 데이터 준비
        Map<String, Object> noticeData = new HashMap<>();
        noticeData.put("title", title);
        noticeData.put("content", content);
        noticeData.put("priority", priority);
        
        // 로딩 표시
        btnSave.setEnabled(false);
        btnSave.setText("저장 중...");
        
        // API 호출
        ApiService apiService = ApiClient.getApiService();
        Call<Map<String, Object>> call = apiService.updateNotice(noticeId, noticeData);
        
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnSave.setEnabled(true);
                btnSave.setText("저장");
                
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        // 수정 성공
                        String message = (String) responseBody.get("message");
                        Toast.makeText(getContext(), 
                            message != null ? message : "공지사항이 수정되었습니다", 
                            Toast.LENGTH_SHORT).show();
                        
                        // 수정된 공지사항 상세보기로 돌아가기 (백스택에서 현재 Fragment 제거)
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        // API 에러
                        String message = (String) responseBody.get("message");
                        Toast.makeText(getContext(), 
                            "수정 실패: " + (message != null ? message : "알 수 없는 오류"), 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // HTTP 에러
                    Toast.makeText(getContext(), 
                        "수정 실패: 서버 오류 (HTTP " + response.code() + ")", 
                        Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("저장");
                
                // 네트워크 에러
                Toast.makeText(getContext(), 
                    "수정 실패: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
}
