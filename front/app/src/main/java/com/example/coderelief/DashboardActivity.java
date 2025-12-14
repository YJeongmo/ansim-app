package com.example.coderelief;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.coderelief.fragments.CaregiverMainFragment;
import com.example.coderelief.fragments.GuardianMainFragment;
import com.example.coderelief.fragments.FindCareCenterFragment;
import com.example.coderelief.fragments.ConsultationFragment;

public class DashboardActivity extends AppCompatActivity {
    
    private String userRole;
    private String username;
    private Long institutionId;
    private Long guardianId;
    private Long caregiverId;
    private Boolean hasGuardianData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        // Get user role from intent
        userRole = getIntent().getStringExtra("user_role");
        username = getIntent().getStringExtra("username");
        institutionId = getIntent().getLongExtra("institution_id", -1L);
        guardianId = getIntent().getLongExtra("guardian_id", -1L);
        caregiverId = getIntent().getLongExtra("caregiver_id", -1L);
        hasGuardianData = getIntent().getBooleanExtra("has_guardian_data", true);
        
        // 로그 추가
        android.util.Log.d("DashboardActivity", "사용자 역할: " + userRole);
        android.util.Log.d("DashboardActivity", "사용자명: " + username);
        android.util.Log.d("DashboardActivity", "요양원 ID: " + institutionId);
        
        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }
    
    private void loadInitialFragment() {
        Fragment initialFragment;
        
        // Check if specific initial fragment requested
        String initialFragmentType = getIntent().getStringExtra("initial_fragment");
        
        if ("consultation".equals(initialFragmentType)) {
            ConsultationFragment consultationFragment = new ConsultationFragment();
            
            // 상담신청 Fragment에 기본 요양원 이름과 이전 Fragment 정보 설정
            Bundle consultationArgs = new Bundle();
            consultationArgs.putString("institution_name", "일반 상담");
            consultationArgs.putString("care_center_name", "일반 상담");
            consultationArgs.putString("name", "일반 상담");
            consultationArgs.putString("previous_fragment_tag", "MainMenu");
            consultationFragment.setArguments(consultationArgs);
            
            initialFragment = consultationFragment;
        } else if ("find_care_center".equals(initialFragmentType)) {
            initialFragment = new FindCareCenterFragment();
        } else if ("guardian".equals(userRole)) {
            initialFragment = new GuardianMainFragment();
        } else {
            initialFragment = new CaregiverMainFragment();
        }
        
        Bundle args = new Bundle();
        args.putString("username", username);
        if (userRole != null) {
            args.putString("user_role", userRole);
        }
        if (guardianId != -1L) {
            args.putLong("guardian_id", guardianId);
        }
        if (hasGuardianData != null) {
            args.putBoolean("has_guardian_data", hasGuardianData);
        }
        initialFragment.setArguments(args);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, initialFragment);
        transaction.commit();
    }
    
    /**
     * Fragment 스택 관리를 개선한 navigateToFragment 메서드
     * 중복 Fragment 방지 및 적절한 백스택 관리
     */
    public void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // 현재 Fragment를 백스택에서 제거 (중복 방지)
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            // 현재 Fragment가 새로 이동하려는 Fragment와 같은 타입이면 제거하지 않음
            if (!currentFragment.getClass().equals(fragment.getClass())) {
                transaction.remove(currentFragment);
            }
        }
        
        transaction.replace(R.id.fragment_container, fragment);
        
        // Fragment 클래스명을 태그로 사용하여 백스택 관리 개선
        String fragmentTag = fragment.getClass().getSimpleName();
        transaction.addToBackStack(fragmentTag);
        
        transaction.commit();
    }
    
    /**
     * 공지사항 목록으로 이동 (백스택 초기화)
     * WriteNoticeFragment, NoticeDetailFragment에서 사용
     */
    public void navigateToNoticeList(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // 백스택 초기화
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        // 현재 Fragment 제거
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            transaction.remove(currentFragment);
        }
        
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        
        transaction.commit();
    }
    
    /**
     * 안드로이드 뒤로가기 동작 커스터마이징
     * Fragment 계층 구조에 따른 적절한 뒤로가기 처리
     */
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        
        if (fragmentManager.getBackStackEntryCount() > 1) {
            // 백스택에 Fragment가 2개 이상 있으면 pop
            fragmentManager.popBackStack();
        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            // 백스택에 Fragment가 1개만 있으면 메인 Fragment로 돌아가기
            loadInitialFragment();
            // 백스택 초기화
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            // 백스택이 비어있으면 앱 종료
            super.onBackPressed();
        }
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public String getUsername() {
        return username;
    }
    
    public Long getInstitutionId() {
        return institutionId;
    }
    
    public Long getCaregiverId() {
        return caregiverId;
    }
}