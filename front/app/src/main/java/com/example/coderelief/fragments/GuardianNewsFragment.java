package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;

public class GuardianNewsFragment extends Fragment {
    
    private TextView tvTitle;
    private CardView cardNoticeList, cardPatientPhotos, cardHealthUpdates;
    private CardView cardActivities, cardMealMenu, cardEvents;
    private RecyclerView rvRecentUpdates;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guardian_news, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        setupRecyclerView();
        
        // TODO: Load recent updates data from database/API
        loadRecentUpdates();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        cardNoticeList = view.findViewById(R.id.card_notice_list);
        cardPatientPhotos = view.findViewById(R.id.card_patient_photos);
        cardHealthUpdates = view.findViewById(R.id.card_health_updates);
        cardActivities = view.findViewById(R.id.card_activities);
        cardMealMenu = view.findViewById(R.id.card_meal_menu);
        cardEvents = view.findViewById(R.id.card_events);
        rvRecentUpdates = view.findViewById(R.id.rv_recent_updates);
    }
    
    private void setupClickListeners() {
        cardNoticeList.setOnClickListener(v -> {
            // Navigate to notice list with guardian role
            NoticeListFragment fragment = new NoticeListFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "guardian");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        cardPatientPhotos.setOnClickListener(v -> {
            // TODO: Navigate to patient photos gallery
            navigateToPatientPhotos();
        });
        
        cardHealthUpdates.setOnClickListener(v -> {
            // TODO: Navigate to health updates
            navigateToHealthUpdates();
        });
        
        cardActivities.setOnClickListener(v -> {
            // TODO: Navigate to activity history
            navigateToActivities();
        });
        
        cardMealMenu.setOnClickListener(v -> {
            // TODO: Navigate to meal menu
            navigateToMealMenu();
        });
        
        cardEvents.setOnClickListener(v -> {
            // TODO: Navigate to events and news
            navigateToEvents();
        });
    }
    
    private void setupRecyclerView() {
        rvRecentUpdates.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Set adapter with recent updates data
        // RecentUpdatesAdapter adapter = new RecentUpdatesAdapter(updatesList, this::onUpdateClick);
        // rvRecentUpdates.setAdapter(adapter);
    }
    
    private void loadRecentUpdates() {
        // TODO: Load recent updates from database/API
        // This should include various types of updates like:
        // - New notices
        // - Health status changes
        // - New photos
        // - Activity reports
        // - Meal updates
        // - Event announcements
        
        // For now, show placeholder message
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "최근 업데이트를 불러오는 중...", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToPatientPhotos() {
        // TODO: Create and navigate to PatientPhotosFragment
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "입소자 사진 갤러리 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToHealthUpdates() {
        // TODO: Create and navigate to HealthUpdatesFragment or PatientListFragment with health filter
        PatientListFragment fragment = new PatientListFragment();
        Bundle args = new Bundle();
        args.putString("user_role", "guardian");
        args.putString("filter_type", "health_updates");
        fragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    private void navigateToActivities() {
        // TODO: Create and navigate to ActivitiesFragment
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "활동내역 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToMealMenu() {
        // TODO: Create and navigate to MealMenuFragment
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "식단표 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToEvents() {
        // TODO: Create and navigate to EventsFragment
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), 
                "행사소식 기능은 준비 중입니다", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void onUpdateClick(String updateType, int updateId) {
        // Handle recent update item click
        switch (updateType) {
            case "notice":
                // Navigate to specific notice
                // TODO: Navigate to notice detail
                break;
            case "health":
                // Navigate to health update
                navigateToHealthUpdates();
                break;
            case "photo":
                // Navigate to photo gallery
                navigateToPatientPhotos();
                break;
            case "activity":
                // Navigate to activities
                navigateToActivities();
                break;
            case "meal":
                // Navigate to meal menu
                navigateToMealMenu();
                break;
            case "event":
                // Navigate to events
                navigateToEvents();
                break;
            default:
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), 
                        "업데이트 상세보기 기능은 준비 중입니다", 
                        android.widget.Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh recent updates when returning to this fragment
        loadRecentUpdates();
    }
}