package com.example.coderelief.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coderelief.R;
import com.example.coderelief.adapters.NotificationAdapter;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.NotificationApiService;
import com.example.coderelief.models.Notification;
import com.example.coderelief.DashboardActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 알림 목록을 표시하는 Fragment
 */
public class NotificationListFragment extends Fragment {
    
    private static final String TAG = "NotificationListFragment";
    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_USER_TYPE = "user_type";
    
    private RecyclerView rvNotifications;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    
    private Long userId;
    private String userType;
    private NotificationApiService apiService;

    public static NotificationListFragment newInstance(Long userId, String userType) {
        NotificationListFragment fragment = new NotificationListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        args.putString(ARG_USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(ARG_USER_ID);
            userType = getArguments().getString(ARG_USER_TYPE);
        }
        apiService = ApiClient.getNotificationApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadNotifications();
    }

    private void initViews(View view) {
        rvNotifications = view.findViewById(R.id.rv_notifications);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList, this::onNotificationClick);
        
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(notificationAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
    }

    private void loadNotifications() {
        if (userId == null || userType == null) {
            Toast.makeText(getContext(), "사용자 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        swipeRefreshLayout.setRefreshing(true);
        
        Call<Map<String, Object>> call = apiService.getNotifications(userId, userType);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        Object dataObject = responseBody.get("data");
                        if (dataObject != null) {
                            try {
                                // Convert data to List<Notification>
                                Gson gson = ApiClient.getGson();
                                String jsonString = gson.toJson(dataObject);
                                Type listType = new TypeToken<List<Notification>>(){}.getType();
                                List<Notification> notifications = gson.fromJson(jsonString, listType);
                                
                                updateNotificationList(notifications);
                                
                                Object countObj = responseBody.get("count");
                                int count = countObj instanceof Double ? ((Double) countObj).intValue() : 
                                           countObj instanceof Integer ? (Integer) countObj : 0;
                                
                                Log.d(TAG, "알림 목록 로드 성공: " + count + "개");
                                
                            } catch (Exception e) {
                                Log.e(TAG, "알림 데이터 파싱 오류", e);
                                Toast.makeText(getContext(), "알림 데이터 처리 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            updateNotificationList(new ArrayList<>());
                        }
                    } else {
                        String message = (String) responseBody.get("message");
                        Toast.makeText(getContext(), message != null ? message : "알림을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "알림 로드 실패: " + response.code());
                    Toast.makeText(getContext(), "서버 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "네트워크 오류", t);
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNotificationList(List<Notification> notifications) {
        if (notifications != null) {
            notificationList.clear();
            notificationList.addAll(notifications);
            notificationAdapter.notifyDataSetChanged();
            
            // 빈 목록 처리
            if (notifications.isEmpty()) {
                Toast.makeText(getContext(), "새로운 알림이 없습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onNotificationClick(Notification notification) {
        // 알림 클릭 시 해당 화면으로 이동
        Log.d(TAG, "알림 클릭: " + notification.getTitle() + ", 타입: " + notification.getNotificationType() + ", 관련ID: " + notification.getRelatedId());
        
        // 알림을 읽음 처리
        markNotificationAsRead(notification.getNotificationId());
        
        try {
            switch (notification.getNotificationType()) {
                case "CHAT":
                    // 채팅 화면으로 이동
                    navigateToChat(notification);
                    break;
                case "APPOINTMENT":
                    // 상담 예약 화면으로 이동
                    navigateToAppointment(notification);
                    break;
                case "CONSULTATION":
                    // 상담 요청 화면으로 이동
                    navigateToConsultation(notification);
                    break;
                case "NOTICE":
                    // 공지사항 화면으로 이동
                    navigateToNotice(notification);
                    break;
                case "MEAL":
                case "ACTIVITY":
                    // 활동/급여 상세 화면으로 이동
                    navigateToActivity(notification);
                    break;
                default:
                    Toast.makeText(getContext(), notification.getTitle(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "알림 클릭 처리 중 오류", e);
            Toast.makeText(getContext(), "화면 이동 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToChat(Notification notification) {
        // 채팅 알림의 경우 relatedId가 chatRoomId
        Long chatRoomId = notification.getRelatedId();
        if (chatRoomId == null) {
            Toast.makeText(getContext(), "채팅방 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 채팅 Fragment로 이동
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong("chat_room_id", chatRoomId);
        args.putString("chat_type", userType.toLowerCase());
        args.putString("user_role", userType.toLowerCase()); // 추가: user_role도 전달
        
        // 사용자 정보 전달
        if (userType.equals("GUARDIAN")) {
            args.putLong("guardian_id", userId);
        } else {
            args.putLong("caregiver_id", userId);
        }
        
        chatFragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(chatFragment);
    }
    
    private void navigateToAppointment(Notification notification) {
        // 상담 예약 알림의 경우 ScheduleFragment로 이동
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString("user_role", userType.toLowerCase());
        
        if (userType.equals("GUARDIAN")) {
            args.putLong("guardian_id", userId);
        } else {
            args.putLong("caregiver_id", userId);
        }
        
        // 특정 예약 ID가 있으면 해당 예약으로 스크롤하도록 설정
        if (notification.getRelatedId() != null) {
            args.putLong("highlight_reservation_id", notification.getRelatedId());
        }
        
        scheduleFragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(scheduleFragment);
    }
    
    private void navigateToConsultation(Notification notification) {
        // 상담 요청 알림의 경우 상담 요청 목록으로 이동
        if (userType.equals("CAREGIVER")) {
            // 요양보호사의 경우 상담 요청 목록으로 이동
            ConsultationRequestListFragment fragment = new ConsultationRequestListFragment();
            Bundle args = new Bundle();
            args.putLong("institution_id", getInstitutionId());
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        } else {
            // 보호자의 경우 상담 신청 화면으로 이동
            ConsultationFragment fragment = new ConsultationFragment();
            Bundle args = new Bundle();
            args.putString("institution_name", "일반 상담");
            args.putString("care_center_name", "일반 상담");
            args.putString("name", "일반 상담");
            args.putString("previous_fragment_tag", "NotificationList");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        }
    }
    
    private void navigateToNotice(Notification notification) {
        // 공지사항 알림의 경우 공지사항 목록으로 이동
        NoticeListFragment fragment = new NoticeListFragment();
        Bundle args = new Bundle();
        args.putString("user_role", userType.toLowerCase());
        fragment.setArguments(args);
        ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
    }
    
    private void navigateToActivity(Notification notification) {
        // 활동/급여 알림의 경우 환자 상세 화면으로 이동
        if (userType.equals("GUARDIAN")) {
            // 보호자의 경우 환자 목록으로 이동
            PatientListFragment fragment = new PatientListFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "guardian");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        } else {
            // 요양보호사의 경우 환자 목록으로 이동
            PatientListFragment fragment = new PatientListFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "caregiver");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        }
    }
    
    private Long getInstitutionId() {
        // DashboardActivity에서 institutionId 가져오기
        if (getActivity() instanceof DashboardActivity) {
            return ((DashboardActivity) getActivity()).getInstitutionId();
        }
        return 1L; // 기본값
    }

    /**
     * 알림을 읽음 처리
     */
    private void markNotificationAsRead(Long notificationId) {
        if (notificationId == null) {
            return;
        }

        Call<Map<String, Object>> call = apiService.markAsRead(notificationId);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");
                    
                    if (success != null && success) {
                        Log.d(TAG, "알림 읽음 처리 성공: notificationId=" + notificationId);
                        
                        // 로컬 리스트에서 해당 알림의 isRead 상태 업데이트
                        for (Notification notification : notificationList) {
                            if (notification.getNotificationId().equals(notificationId)) {
                                notification.setRead(true);
                                break;
                            }
                        }
                        
                        // UI 업데이트
                        if (notificationAdapter != null) {
                            notificationAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "알림 읽음 처리 실패: " + responseBody.get("message"));
                    }
                } else {
                    Log.e(TAG, "알림 읽음 처리 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "알림 읽음 처리 네트워크 오류", t);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fragment가 다시 보일 때 알림 목록 새로고침
        loadNotifications();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Fragment를 떠날 때 메인 화면의 알림 배지 업데이트
        updateMainScreenBadge();
    }

    /**
     * 메인 화면의 알림 배지 업데이트
     */
    private void updateMainScreenBadge() {
        if (getActivity() instanceof DashboardActivity) {
            DashboardActivity activity = (DashboardActivity) getActivity();
            
            // 메인 화면 Fragment 찾기
            if ("GUARDIAN".equals(userType)) {
                GuardianMainFragment mainFragment = findFragmentInBackStack(GuardianMainFragment.class);
                if (mainFragment != null && mainFragment.isAdded()) {
                    mainFragment.checkNotifications();
                }
            } else if ("CAREGIVER".equals(userType)) {
                CaregiverMainFragment mainFragment = findFragmentInBackStack(CaregiverMainFragment.class);
                if (mainFragment != null && mainFragment.isAdded()) {
                    mainFragment.checkNotifications();
                }
            }
        }
    }

    /**
     * 백스택에서 특정 Fragment 찾기
     */
    @SuppressWarnings("unchecked")
    private <T extends Fragment> T findFragmentInBackStack(Class<T> fragmentClass) {
        if (getActivity() == null) {
            return null;
        }
        
        try {
            androidx.fragment.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            java.util.List<Fragment> fragments = fragmentManager.getFragments();
            
            for (Fragment fragment : fragments) {
                if (fragment != null && fragmentClass.isInstance(fragment)) {
                    return (T) fragment;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Fragment 찾기 실패", e);
        }
        
        return null;
    }
}