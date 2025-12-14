package com.example.coderelief.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.NotificationApiService;
import com.example.coderelief.utils.PermissionUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaregiverMainFragment extends Fragment {
    
    private LinearLayout btnRecipientList, btnNotices, btnScheduleManagement, btnConsultationRequests;
    private ImageButton btnNotifications;
    private View notificationBadge;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caregiver_main, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        checkNotifications();
        setupMenuVisibility(); // 권한에 따른 메뉴 표시 제어
    }
    
    private void initViews(View view) {
        btnRecipientList = view.findViewById(R.id.btn_recipient_list);
        btnNotices = view.findViewById(R.id.btn_notices);
        btnScheduleManagement = view.findViewById(R.id.btn_schedule_management);
        btnConsultationRequests = view.findViewById(R.id.btn_consultation_requests);
        btnNotifications = view.findViewById(R.id.btn_notifications);
        notificationBadge = view.findViewById(R.id.notification_badge);
    }
    
    private void setupClickListeners() {
        btnRecipientList.setOnClickListener(v -> {
            PatientListFragment fragment = new PatientListFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "caregiver");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnNotices.setOnClickListener(v -> {
            // Navigate to notice list with caregiver role for reading and management
            NoticeListFragment fragment = new NoticeListFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "caregiver");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnScheduleManagement.setOnClickListener(v -> {
            ScheduleFragment fragment = new ScheduleFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "caregiver");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnConsultationRequests.setOnClickListener(v -> {
            // 권한 체크 후 접근 허용
            if (PermissionUtils.canAccessConsultations(requireContext())) {
                // DashboardActivity에서 institutionId를 가져와서 전달
                DashboardActivity dashboardActivity = (DashboardActivity) requireActivity();
                Long institutionId = dashboardActivity.getInstitutionId();
                ConsultationRequestListFragment fragment = ConsultationRequestListFragment.newInstance(institutionId);
                dashboardActivity.navigateToFragment(fragment);
            } else {
                // 권한이 없는 경우 토스트 메시지 표시
                String caregiverRole = PermissionUtils.getCaregiverRole(requireContext());
                String caregiverName = PermissionUtils.getCaregiverName(requireContext());
                String message = String.format("%s님은 %s 등급으로 비회원 상담 신청 메뉴에 접근할 수 없습니다.", 
                        caregiverName.isEmpty() ? "현재 사용자" : caregiverName, caregiverRole);
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                Log.w("CaregiverMainFragment", "권한 없음: " + caregiverRole + " 등급으로 상담신청 메뉴 접근 시도");
            }
        });
        
        btnNotifications.setOnClickListener(v -> {
            // 알림 목록으로 이동
            DashboardActivity dashboardActivity = (DashboardActivity) requireActivity();
            // TODO: CaregiverMainFragment에서 caregiverId를 가져오는 방법 구현 필요
            // 임시로 institutionId 사용 (추후 수정 필요)
            Long institutionId = dashboardActivity.getInstitutionId();
            if (institutionId != null && institutionId > 0) {
                NotificationListFragment fragment = NotificationListFragment.newInstance(institutionId, "CAREGIVER");
                dashboardActivity.navigateToFragment(fragment);
            } else {
                Toast.makeText(getContext(), "사용자 정보를 확인할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void checkNotifications() {
        DashboardActivity dashboardActivity = (DashboardActivity) requireActivity();
        Long caregiverId = dashboardActivity.getCaregiverId();

        if (caregiverId != null && caregiverId > 0) {
            NotificationApiService apiService = ApiClient.getNotificationApiService();
            // 미읽은 알림 개수로 변경
            Call<Map<String, Object>> call = apiService.getUnreadNotificationCount(caregiverId, "CAREGIVER");
            
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> responseBody = response.body();
                        Object countObj = responseBody.get("count");
                        
                        int count = 0;
                        if (countObj instanceof Double) {
                            count = ((Double) countObj).intValue();
                        } else if (countObj instanceof Integer) {
                            count = (Integer) countObj;
                        } else if (countObj instanceof Long) {
                            count = ((Long) countObj).intValue();
                        }
                        
                        Log.d("CaregiverMainFragment", "미읽은 알림 개수: " + count);
                        updateNotificationBadge(count > 0);
                    }
                }
                
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e("CaregiverMainFragment", "알림 개수 조회 실패", t);
                }
            });
        }
    }
    
    private void updateNotificationBadge(boolean hasNotifications) {
        if (notificationBadge != null) {
            notificationBadge.setVisibility(hasNotifications ? View.VISIBLE : View.GONE);
        }
    }
    
    /**
     * 권한에 따른 메뉴 표시 제어
     */
    private void setupMenuVisibility() {
        // 현재 권한 정보 로그 출력
        PermissionUtils.logCurrentPermissions(requireContext());
        
        // 비회원 상담 신청 메뉴 표시 제어
        boolean canAccessConsultations = PermissionUtils.canAccessConsultations(requireContext());
        
        if (canAccessConsultations) {
            // 권한이 있는 경우: 메뉴 표시
            btnConsultationRequests.setVisibility(View.VISIBLE);
            Log.d("CaregiverMainFragment", "비회원 상담 신청 메뉴 표시됨 (권한 있음)");
        } else {
            // 권한이 없는 경우: 메뉴 숨김
            btnConsultationRequests.setVisibility(View.GONE);
            Log.d("CaregiverMainFragment", "비회원 상담 신청 메뉴 숨김 (권한 없음)");
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        checkNotifications(); // Fragment가 다시 보일 때 알림 상태 업데이트
        setupMenuVisibility(); // Fragment가 다시 보일 때 메뉴 표시 상태 업데이트
    }
}