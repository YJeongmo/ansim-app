package com.example.coderelief.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 사용자 권한 관리 유틸리티 클래스
 * 로그인 시 받은 권한 정보를 저장하고 관리
 */
public class PermissionUtils {
    
    private static final String PREF_NAME = "user_permissions";
    private static final String KEY_CAREGIVER_ROLE = "caregiver_role";
    private static final String KEY_CAN_ACCESS_CONSULTATIONS = "can_access_consultations";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_CAREGIVER_NAME = "caregiver_name";
    private static final String KEY_INSTITUTION_ID = "institution_id";
    
    /**
     * 로그인 시 받은 권한 정보를 SharedPreferences에 저장
     */
    public static void saveUserPermissions(Context context, String userRole, String caregiverRole, 
                                         boolean canAccessConsultations, String caregiverName, Long institutionId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString(KEY_USER_ROLE, userRole);
        editor.putString(KEY_CAREGIVER_ROLE, caregiverRole);
        editor.putBoolean(KEY_CAN_ACCESS_CONSULTATIONS, canAccessConsultations);
        editor.putString(KEY_CAREGIVER_NAME, caregiverName);
        editor.putLong(KEY_INSTITUTION_ID, institutionId != null ? institutionId : -1L);
        
        editor.apply();
        
        Log.d("PermissionUtils", "권한 정보 저장 완료: " +
                "userRole=" + userRole + 
                ", caregiverRole=" + caregiverRole + 
                ", canAccessConsultations=" + canAccessConsultations +
                ", caregiverName=" + caregiverName +
                ", institutionId=" + institutionId);
    }
    
    /**
     * 비회원 상담 신청 메뉴에 접근할 수 있는지 확인
     */
    public static boolean canAccessConsultations(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean canAccess = prefs.getBoolean(KEY_CAN_ACCESS_CONSULTATIONS, false);
        
        Log.d("PermissionUtils", "상담신청 접근 권한 확인: " + canAccess);
        return canAccess;
    }
    
    /**
     * 요양원 직원의 등급을 반환
     */
    public static String getCaregiverRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CAREGIVER_ROLE, "STAFF");
    }
    
    /**
     * 사용자 역할을 반환 (guardian, caregiver)
     */
    public static String getUserRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ROLE, "");
    }
    
    /**
     * 직원 이름을 반환
     */
    public static String getCaregiverName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CAREGIVER_NAME, "");
    }
    
    /**
     * 요양원 ID를 반환
     */
    public static Long getInstitutionId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long id = prefs.getLong(KEY_INSTITUTION_ID, -1L);
        return id == -1L ? null : id;
    }
    
    /**
     * 특정 등급 이상의 권한을 가지고 있는지 확인
     */
    public static boolean hasRoleOrHigher(Context context, String requiredRole) {
        String currentRole = getCaregiverRole(context);
        
        // 등급 우선순위: STAFF < MANAGER < ADMIN
        int currentLevel = getRoleLevel(currentRole);
        int requiredLevel = getRoleLevel(requiredRole);
        
        boolean hasPermission = currentLevel >= requiredLevel;
        Log.d("PermissionUtils", "권한 확인: " + currentRole + " >= " + requiredRole + " = " + hasPermission);
        
        return hasPermission;
    }
    
    /**
     * 등급을 숫자로 변환 (높을수록 권한이 높음)
     */
    private static int getRoleLevel(String role) {
        switch (role) {
            case "STAFF":
                return 1;
            case "MANAGER":
                return 2;
            case "ADMIN":
                return 3;
            default:
                return 0;
        }
    }
    
    /**
     * 저장된 권한 정보를 모두 삭제 (로그아웃 시 사용)
     */
    public static void clearPermissions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Log.d("PermissionUtils", "권한 정보 삭제 완료");
    }
    
    /**
     * 권한 정보 디버깅용 로그 출력
     */
    public static void logCurrentPermissions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        Log.d("PermissionUtils", "=== 현재 권한 정보 ===");
        Log.d("PermissionUtils", "User Role: " + prefs.getString(KEY_USER_ROLE, "없음"));
        Log.d("PermissionUtils", "Caregiver Role: " + prefs.getString(KEY_CAREGIVER_ROLE, "없음"));
        Log.d("PermissionUtils", "Can Access Consultations: " + prefs.getBoolean(KEY_CAN_ACCESS_CONSULTATIONS, false));
        Log.d("PermissionUtils", "Caregiver Name: " + prefs.getString(KEY_CAREGIVER_NAME, "없음"));
        Log.d("PermissionUtils", "Institution ID: " + prefs.getLong(KEY_INSTITUTION_ID, -1L));
    }
}

