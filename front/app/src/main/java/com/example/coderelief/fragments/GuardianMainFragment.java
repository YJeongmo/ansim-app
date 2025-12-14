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

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuardianMainFragment extends Fragment {
    
    private LinearLayout btnPatientList, btnChat, btnSchedule, btnNotices;
    private ImageButton btnNotifications;
    private View notificationBadge;
    private TextView tvFamilyNameMain, tvFamilyBasicInfo;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guardian_main, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        loadFamilyInfo();
        checkGuardianDataStatus();
        checkNotifications();
    }
    
    private void initViews(View view) {
        btnPatientList = view.findViewById(R.id.btn_patient_list);
        btnChat = view.findViewById(R.id.btn_chat);
        btnSchedule = view.findViewById(R.id.btn_schedule);
        btnNotices = view.findViewById(R.id.btn_notices);
        btnNotifications = view.findViewById(R.id.btn_notifications);
        notificationBadge = view.findViewById(R.id.notification_badge);
        tvFamilyNameMain = view.findViewById(R.id.tv_family_name_main);
        tvFamilyBasicInfo = view.findViewById(R.id.tv_family_basic_info);
    }
    
    private void setupClickListeners() {
        btnPatientList.setOnClickListener(v -> {
            // 바로 환자 상세 정보로 이동 (첫 번째 환자)
            GuardianPatientDetailFragment fragment = new GuardianPatientDetailFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "guardian");
            // 기본 환자 정보 설정 (실제로는 API에서 가져와야 함)
            args.putLong("patient_id", 1L);
            args.putString("patient_name", "김영희");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnChat.setOnClickListener(v -> {
            // 바로 채팅창으로 이동 (기본 환자와의 채팅)
            ChatFragment fragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("chat_type", "care_center");
            args.putLong("patient_id", 1L);
            args.putString("patient_name", "김영희");
            args.putString("user_role", "guardian");
            args.putString("care_center_name", "행복한 노인요양원");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnSchedule.setOnClickListener(v -> {
            ScheduleFragment fragment = new ScheduleFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "guardian");
            
            // Pass guardian_id from parent arguments
            Bundle parentArgs = getArguments();
            if (parentArgs != null && parentArgs.containsKey("guardian_id")) {
                args.putLong("guardian_id", parentArgs.getLong("guardian_id"));
            }
            
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnNotices.setOnClickListener(v -> {
            // 공지사항 목록으로 이동 (보호자 권한)
            NoticeListFragment fragment = new NoticeListFragment();
            Bundle args = new Bundle();
            args.putString("user_role", "guardian");
            fragment.setArguments(args);
            ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
        });
        
        btnNotifications.setOnClickListener(v -> {
            // 알림 목록으로 이동
            Bundle args = getArguments();
            if (args != null) {
                Long guardianId = args.getLong("guardian_id", 0L);
                if (guardianId > 0) {
                    NotificationListFragment fragment = NotificationListFragment.newInstance(guardianId, "GUARDIAN");
                    ((DashboardActivity) requireActivity()).navigateToFragment(fragment);
                } else {
                    Toast.makeText(getContext(), "사용자 정보를 확인할 수 없습니다", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "사용자 정보가 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void checkGuardianDataStatus() {
        Bundle args = getArguments();
        if (args != null) {
            boolean hasGuardianData = args.getBoolean("has_guardian_data", true);
            
            if (!hasGuardianData) {
                // 환자 데이터가 없는 경우 버튼 비활성화 및 메시지 표시
                btnPatientList.setEnabled(false);
                btnPatientList.setAlpha(0.5f);
                
                // LinearLayout 내부의 TextView 찾아서 텍스트 변경
                TextView textView = btnPatientList.findViewById(android.R.id.text1);
                if (textView == null) {
                    // TextView를 찾지 못한 경우, LinearLayout의 자식 중 TextView를 찾기
                    for (int i = 0; i < btnPatientList.getChildCount(); i++) {
                        View child = btnPatientList.getChildAt(i);
                        if (child instanceof TextView) {
                            textView = (TextView) child;
                            break;
                        }
                    }
                }
                if (textView != null) {
                    textView.setText("우리 가족\n(연결된 환자 정보 없음)");
                }
                
                // 사용자에게 안내 메시지 표시
                Toast.makeText(getContext(), "회원가입이 완료되었습니다. 환자 정보를 연결하려면 요양원에 문의하세요.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public void checkNotifications() {
        Bundle args = getArguments();
        if (args != null) {
            Long guardianId = args.getLong("guardian_id", 0L);
            if (guardianId > 0) {
                NotificationApiService apiService = ApiClient.getNotificationApiService();
                // 미읽은 알림 개수로 변경
                Call<Map<String, Object>> call = apiService.getUnreadNotificationCount(guardianId, "GUARDIAN");
                
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
                            
                            Log.d("GuardianMainFragment", "미읽은 알림 개수: " + count);
                            updateNotificationBadge(count > 0);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Log.e("GuardianMainFragment", "알림 개수 조회 실패", t);
                    }
                });
            }
        }
    }
    
    private void updateNotificationBadge(boolean hasNotifications) {
        if (notificationBadge != null) {
            notificationBadge.setVisibility(hasNotifications ? View.VISIBLE : View.GONE);
        }
    }
    
    private void loadFamilyInfo() {
        // 실제 구현에서는 API에서 가족 정보를 가져와야 함
        // 현재는 테스트용 하드코딩된 데이터 사용
        Bundle args = getArguments();
        if (args != null) {
            String patientName = args.getString("patient_name", "김영희");
            Long patientId = args.getLong("patient_id", 1L);
            
            // 가족 이름 설정
            tvFamilyNameMain.setText(patientName + "님");
            
            // 기본 정보 설정 (실제로는 API에서 가져와야 함)
            String basicInfo = getFamilyBasicInfo(patientId);
            tvFamilyBasicInfo.setText(basicInfo);
        } else {
            // 기본값 설정
            tvFamilyNameMain.setText("김영희님");
            tvFamilyBasicInfo.setText("나이: 80세 | 방호실: 101호\n요양등급: 2등급 | 입소일: 2023-01-15");
        }
    }
    
    private String getFamilyBasicInfo(Long patientId) {
        // 실제 구현에서는 patientId를 사용해 API에서 데이터를 가져와야 함
        // 현재는 하드코딩된 데이터 반환
        switch (patientId.intValue()) {
            case 1:
                return "나이: 80세 | 방호실: 101호\n요양등급: 2등급 | 입소일: 2023-01-15";
            case 2:
                return "나이: 75세 | 방호실: 102호\n요양등급: 3등급 | 입소일: 2023-02-20";
            case 3:
                return "나이: 85세 | 방호실: 103호\n요양등급: 1등급 | 입소일: 2023-03-10";
            default:
                return "나이: 80세 | 방호실: 101호\n요양등급: 2등급 | 입소일: 2023-01-15";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNotifications(); // Fragment가 다시 보일 때 알림 상태 업데이트
    }
}