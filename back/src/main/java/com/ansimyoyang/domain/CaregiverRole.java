package com.ansimyoyang.domain;

/**
 * 요양원 직원 등급
 * 
 * STAFF: 일반 직원 - 기본 기능만 사용 가능
 * MANAGER: 관리자 - 비회원 상담 신청 접근 가능
 * ADMIN: 최고 관리자 - 모든 권한 보유
 */
public enum CaregiverRole {
    /**
     * 일반 직원
     * - 환자 정보 조회/수정
     * - 일일 기록 작성
     * - 활동 기록 작성
     * - 공지사항 조회
     */
    STAFF,
    
    /**
     * 관리자
     * - STAFF의 모든 권한
     * - 비회원 상담 신청 조회/관리 ⭐
     * - 공지사항 작성
     */
    MANAGER,
    
    /**
     * 최고 관리자
     * - MANAGER의 모든 권한
     * - 직원 관리
     * - 시스템 설정
     */
    ADMIN;
    
    /**
     * 비회원 상담 신청에 접근할 수 있는지 확인
     */
    public boolean canAccessConsultations() {
        return this == MANAGER || this == ADMIN;
    }
    
    /**
     * 직원 관리 권한이 있는지 확인
     */
    public boolean canManageStaff() {
        return this == ADMIN;
    }
    
    /**
     * 특정 역할 이상의 권한을 가지고 있는지 확인
     */
    public boolean hasRoleOrHigher(CaregiverRole requiredRole) {
        return this.ordinal() >= requiredRole.ordinal();
    }
}



