package com.example.coderelief.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteNoticeFragment extends Fragment {
    
    private TextView tvTitle;
    private Spinner spinnerCategory;
    private RadioGroup rgPriority;
    private RadioButton rbNormal, rbImportant, rbUrgent;
    private TextInputLayout tilTitle, tilContent;
    private TextInputEditText etTitle, etContent;
    private Button btnAttachImage, btnAttachFile, btnSaveDraft, btnPreview, btnPublish;
    private CheckBox cbAllGuardians, cbAllStaff, cbSpecificGroups;
    private CheckBox cbPushNotification, cbSmsNotification;
    private RecyclerView rvAttachments;
    
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String[]> filePickerLauncher;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_write_notice, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupFilePickers();
        setupClickListeners();
        setupTextWatchers();
        setupRecyclerView();
        setupBackPressedCallback();
        
        // Load draft if available
        loadDraftIfAvailable();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        rgPriority = view.findViewById(R.id.rg_priority);
        rbNormal = view.findViewById(R.id.rb_normal);
        rbImportant = view.findViewById(R.id.rb_important);
        rbUrgent = view.findViewById(R.id.rb_urgent);
        tilTitle = view.findViewById(R.id.til_title);
        tilContent = view.findViewById(R.id.til_content);
        etTitle = view.findViewById(R.id.et_title);
        etContent = view.findViewById(R.id.et_content);
        btnAttachImage = view.findViewById(R.id.btn_attach_image);
        btnAttachFile = view.findViewById(R.id.btn_attach_file);
        btnSaveDraft = view.findViewById(R.id.btn_save_draft);
        btnPreview = view.findViewById(R.id.btn_preview);
        btnPublish = view.findViewById(R.id.btn_publish);
        cbAllGuardians = view.findViewById(R.id.cb_all_guardians);
        cbAllStaff = view.findViewById(R.id.cb_all_staff);
        cbSpecificGroups = view.findViewById(R.id.cb_specific_groups);
        cbPushNotification = view.findViewById(R.id.cb_push_notification);
        cbSmsNotification = view.findViewById(R.id.cb_sms_notification);
        rvAttachments = view.findViewById(R.id.rv_attachments);
    }
    
    private void setupFilePickers() {
        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    handleImageSelection(uri);
                }
            }
        );
        
        // File picker launcher
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenMultipleDocuments(),
            uris -> {
                if (uris != null && !uris.isEmpty()) {
                    handleFileSelection(uris);
                }
            }
        );
    }
    
    private void setupClickListeners() {
        btnAttachImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
        
        btnAttachFile.setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{"*/*"});
        });
        
        btnSaveDraft.setOnClickListener(v -> {
            saveDraft();
        });
        
        btnPreview.setOnClickListener(v -> {
            showPreview();
        });
        
        btnPublish.setOnClickListener(v -> {
            publishNotice();
        });
        
        // Priority radio button listeners
        rgPriority.setOnCheckedChangeListener((group, checkedId) -> {
            updateNotificationSettings(checkedId);
        });
        
        // Target audience listeners
        cbAllGuardians.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && cbSpecificGroups.isChecked()) {
                cbSpecificGroups.setChecked(false);
            }
        });
        
        cbAllStaff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && cbSpecificGroups.isChecked()) {
                cbSpecificGroups.setChecked(false);
            }
        });
        
        cbSpecificGroups.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbAllGuardians.setChecked(false);
                cbAllStaff.setChecked(false);
                // TODO: Show specific group selection dialog
            }
        });
    }
    
    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        };
        
        etTitle.addTextChangedListener(textWatcher);
        etContent.addTextChangedListener(textWatcher);
    }
    
    private void setupRecyclerView() {
        rvAttachments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAttachments.setNestedScrollingEnabled(false);
        // TODO: Set adapter for attachments
        // AttachmentsAdapter adapter = new AttachmentsAdapter(attachmentsList, this::onAttachmentRemove);
        // rvAttachments.setAdapter(adapter);
    }
    
    private void handleImageSelection(Uri imageUri) {
        // TODO: Add image to attachments list
        // TODO: Show image preview
        // TODO: Update RecyclerView
        showAttachments(true);
        
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "이미지가 추가되었습니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void handleFileSelection(java.util.List<Uri> fileUris) {
        // TODO: Add files to attachments list
        // TODO: Validate file sizes and types
        // TODO: Update RecyclerView
        showAttachments(true);
        
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                fileUris.size() + "개의 파일이 추가되었습니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showAttachments(boolean show) {
        rvAttachments.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void updateNotificationSettings(int checkedId) {
        if (checkedId == R.id.rb_urgent) {
            // Urgent notices should have push notifications enabled by default
            cbPushNotification.setChecked(true);
        }
    }
    
    private void validateForm() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";
        
        boolean isValid = !title.isEmpty() && !content.isEmpty() && 
                         (cbAllGuardians.isChecked() || cbAllStaff.isChecked() || cbSpecificGroups.isChecked());
        
        btnPreview.setEnabled(isValid);
        btnPublish.setEnabled(isValid);
        
        // Update title error
        if (title.isEmpty()) {
            tilTitle.setError("제목을 입력해주세요");
        } else {
            tilTitle.setError(null);
        }
        
        // Update content error
        if (content.isEmpty()) {
            tilContent.setError("내용을 입력해주세요");
        } else {
            tilContent.setError(null);
        }
    }
    
    private void saveDraft() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";
        
        if (title.isEmpty() && content.isEmpty()) {
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), 
                    "저장할 내용이 없습니다", 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
            return;
        }
        
        // TODO: Save draft to database or local storage
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "임시저장되었습니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showPreview() {
        if (!validateFormForSubmission()) {
            return;
        }
        
        // TODO: Show preview dialog or navigate to preview fragment
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "미리보기 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void publishNotice() {
        if (!validateFormForSubmission()) {
            return;
        }
        
        // Get form data
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";
        
        // Get priority
        String priority = "NORMAL";
        if (rbImportant.isChecked()) {
            priority = "IMPORTANT";
        } else if (rbUrgent.isChecked()) {
            priority = "URGENT";
        }
        
        // Prepare API request data
        Map<String, Object> noticeData = new HashMap<>();
        noticeData.put("title", title);
        noticeData.put("content", content);
        noticeData.put("priority", priority);
        noticeData.put("institutionId", 1L); // 임시로 기관 ID 1 사용
        noticeData.put("caregiverId", 1L);   // 임시로 요양보호사 ID 1 사용
        noticeData.put("isPersonal", false); // 전체 공지사항
        
        // Show loading
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "공지사항을 게시하는 중...", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
        
        // Call API
        ApiService apiService = ApiClient.getApiService();
        Call<Map<String, Object>> call = apiService.createNotice(noticeData);
        
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        // Success
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), 
                                "공지사항이 성공적으로 게시되었습니다", 
                                android.widget.Toast.LENGTH_SHORT).show();
                        }
                        
                        // Navigate back to notice list
                        if (getActivity() instanceof DashboardActivity) {
                            ((DashboardActivity) getActivity()).onBackPressed();
                        }
                    } else {
                        // API returned error
                        String message = (String) responseBody.get("message");
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), 
                                "공지사항 게시 실패: " + (message != null ? message : "알 수 없는 오류"), 
                                android.widget.Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    // HTTP error
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), 
                            "공지사항 게시 실패: 서버 오류 (HTTP " + response.code() + ")", 
                            android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Network error
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), 
                        "공지사항 게시 실패: " + t.getMessage(), 
                        android.widget.Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private boolean validateFormForSubmission() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";
        
        if (title.isEmpty()) {
            tilTitle.setError("제목을 입력해주세요");
            etTitle.requestFocus();
            return false;
        }
        
        if (content.isEmpty()) {
            tilContent.setError("내용을 입력해주세요");
            etContent.requestFocus();
            return false;
        }
        
        if (!cbAllGuardians.isChecked() && !cbAllStaff.isChecked() && !cbSpecificGroups.isChecked()) {
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), 
                    "공지 대상을 선택해주세요", 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        
        return true;
    }
    
    private void loadDraftIfAvailable() {
        // TODO: Load draft from database or local storage if available
        // TODO: Populate form fields with draft data
    }
    
    private void onAttachmentRemove(int position) {
        // TODO: Remove attachment from list
        // TODO: Update RecyclerView
        // TODO: Hide attachment section if no attachments
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Auto-save draft if there's content
        saveDraftIfContentExists();
    }
    
    private void saveDraftIfContentExists() {
        String title = etTitle != null && etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent != null && etContent.getText() != null ? etContent.getText().toString().trim() : "";
        
        if (!title.isEmpty() || !content.isEmpty()) {
            // TODO: Auto-save draft
        }
    }
    
    private void setupBackPressedCallback() {
        // 뒤로가기 버튼 동작을 커스터마이징
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 공지사항 작성 페이지에서 뒤로가기 시 항상 공지사항 목록으로 돌아가기
                navigateToNoticeList();
            }
        };
        
        // Fragment에 콜백 등록
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
    
    private void navigateToNoticeList() {
        // 공지사항 목록 Fragment로 이동
        NoticeListFragment noticeListFragment = new NoticeListFragment();
        Bundle args = new Bundle();
        args.putString("user_role", "caregiver"); // 요양보호사용 공지사항 목록
        noticeListFragment.setArguments(args);
        
        if (getActivity() instanceof DashboardActivity) {
            ((DashboardActivity) getActivity()).navigateToNoticeList(noticeListFragment);
        }
    }
}