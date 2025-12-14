package com.example.coderelief.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.adapters.NoticeAdapter;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeListFragment extends Fragment {
    
    private TextView tvTitle, tvNoticeCount, tvUnreadCount;
    private TextInputEditText etSearch;
    private Button btnSearch, btnLoadMore;
    private Spinner spinnerCategory, spinnerSort;
    private LinearLayout layoutImportantNotices, layoutEmptyState;
    private RecyclerView rvImportantNotices, rvNotices;
    private FloatingActionButton fabWriteNotice;
    
    private String userRole;
    private NoticeAdapter noticeAdapter;
    private java.util.List<java.util.Map<String, Object>> noticesList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notice_list, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getUserRole();
        setupRoleBasedVisibility();
        setupClickListeners();
        setupSearch();
        setupRecyclerViews();
        
        // TODO: Load notice data from database/API
        loadNoticeData();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvNoticeCount = view.findViewById(R.id.tv_notice_count);
        tvUnreadCount = view.findViewById(R.id.tv_unread_count);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        btnLoadMore = view.findViewById(R.id.btn_load_more);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerSort = view.findViewById(R.id.spinner_sort);
        layoutImportantNotices = view.findViewById(R.id.layout_important_notices);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        rvImportantNotices = view.findViewById(R.id.rv_important_notices);
        rvNotices = view.findViewById(R.id.rv_notices);
        fabWriteNotice = view.findViewById(R.id.fab_write_notice);
    }
    
    private void getUserRole() {
        Bundle args = getArguments();
        if (args != null) {
            userRole = args.getString("user_role", "guardian");
        } else {
            // Try to get from activity or default
            userRole = "guardian";
        }
    }
    
    private void setupRoleBasedVisibility() {
        if ("caregiver".equals(userRole)) {
            // Caregivers can write notices
            tvTitle.setText("공지사항 관리");
            fabWriteNotice.setVisibility(View.VISIBLE);
        } else {
            // Guardians can only read notices
            tvTitle.setText("공지사항");
            fabWriteNotice.setVisibility(View.GONE);
        }
    }
    
    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> performSearch());
        
        
        fabWriteNotice.setOnClickListener(v -> {
            navigateToWriteNotice();
        });
        
        btnLoadMore.setOnClickListener(v -> {
            // TODO: Load more notices
            loadMoreNotices();
        });
    }
    
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: Implement real-time search filtering
                filterNotices(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupRecyclerViews() {
        // Important notices RecyclerView
        rvImportantNotices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvImportantNotices.setNestedScrollingEnabled(false);
        
        // Regular notices RecyclerView
        rvNotices.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize notices list and adapter
        noticesList = new java.util.ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticesList, this::onNoticeClick);
        rvNotices.setAdapter(noticeAdapter);
    }
    
    private void performSearch() {
        String searchQuery = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        if (!searchQuery.isEmpty()) {
            // TODO: Implement search functionality
            filterNotices(searchQuery);
        }
    }
    
    private void filterNotices(String query) {
        // TODO: Implement filtering logic based on search query and spinner selections
        // This should filter both RecyclerView adapters
        
        // Update notice count after filtering
        updateNoticeCount(0, 0); // Placeholder counts
    }
    
    private void loadNoticeData() {
        // API를 통해 공지사항 데이터 로드
        if (getContext() == null) return;
        
        // 임시로 기관 ID 1을 사용 (실제로는 로그인한 사용자의 기관 ID 사용)
        Long institutionId = 1L;
        
        ApiService apiService = ApiClient.getApiService();
        apiService.getNoticesByInstitution(institutionId, 0, 10, "createdAt", "desc")
                .enqueue(new retrofit2.Callback<java.util.Map<String, Object>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, Object>> call, 
                                         retrofit2.Response<java.util.Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            java.util.Map<String, Object> responseBody = response.body();
                            boolean success = (Boolean) responseBody.get("success");
                            
                            if (success) {
                                java.util.List<java.util.Map<String, Object>> notices = 
                                    (java.util.List<java.util.Map<String, Object>>) responseBody.get("data");
                                
                                if (notices != null && !notices.isEmpty()) {
                                    // 공지사항 데이터가 있으면 UI 업데이트
                                    updateNoticeList(notices);
                                    showEmptyState(false);
                                    updateNoticeCount(notices.size(), 0); // TODO: 미읽음 개수 계산
                                } else {
                                    showEmptyState(true);
                                    updateNoticeCount(0, 0);
                                }
                            } else {
                                showEmptyState(true);
                                updateNoticeCount(0, 0);
                            }
                        } else {
                            showEmptyState(true);
                            updateNoticeCount(0, 0);
                        }
                    }
                    
                    @Override
                    public void onFailure(retrofit2.Call<java.util.Map<String, Object>> call, Throwable t) {
                        // 네트워크 오류 처리
                        showEmptyState(true);
                        updateNoticeCount(0, 0);
                        
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), 
                                "공지사항을 불러오는데 실패했습니다: " + t.getMessage(), 
                                android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    
    private void loadMoreNotices() {
        // TODO: Load additional notices (pagination)
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "더 많은 공지사항을 불러오는 중...", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateNoticeCount(int totalCount, int unreadCount) {
        if (tvNoticeCount != null) {
            tvNoticeCount.setText("총 " + totalCount + "개의 공지사항");
        }
        if (tvUnreadCount != null) {
            tvUnreadCount.setText("미읽음 " + unreadCount + "개");
            tvUnreadCount.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);
        }
    }
    
    private void showEmptyState(boolean show) {
        if (show) {
            rvNotices.setVisibility(View.GONE);
            layoutImportantNotices.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            btnLoadMore.setVisibility(View.GONE);
        } else {
            rvNotices.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            // Show important notices section if there are important notices
            // layoutImportantNotices.setVisibility(hasImportantNotices ? View.VISIBLE : View.GONE);
        }
    }
    
    private void showImportantNotices(boolean show) {
        layoutImportantNotices.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    private void updateNoticeList(java.util.List<java.util.Map<String, Object>> notices) {
        // RecyclerView 어댑터에 공지사항 데이터 설정
        noticesList.clear();
        noticesList.addAll(notices);
        noticeAdapter.updateNotices(noticesList);
    }
    
    private void onNoticeClick(int noticeId, boolean isImportant) {
        android.util.Log.d("NoticeList", "onNoticeClick 호출됨 - noticeId: " + noticeId);
        
        // 공지사항 상세보기로 이동
        NoticeDetailFragment fragment = new NoticeDetailFragment();
        Bundle args = new Bundle();
        args.putString("user_role", userRole);
        args.putLong("notice_id", noticeId);
        fragment.setArguments(args);
        
        android.util.Log.d("NoticeList", "Fragment 생성 및 Arguments 설정 완료");
        
        try {
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
            android.util.Log.d("NoticeList", "navigateToFragment 호출 완료");
        } catch (Exception e) {
            android.util.Log.e("NoticeList", "navigateToFragment 호출 실패: " + e.getMessage(), e);
        }
        
        // TODO: Mark notice as read
        // markNoticeAsRead(noticeId);
    }
    
    private void navigateToWriteNotice() {
        WriteNoticeFragment fragment = new WriteNoticeFragment();
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    private void markNoticeAsRead(int noticeId) {
        // TODO: Mark notice as read in database
        // TODO: Update unread count
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh notice data when returning to this fragment
        loadNoticeData();
    }
}