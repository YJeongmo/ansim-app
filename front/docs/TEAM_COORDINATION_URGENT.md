# 🚨 팀 작업 조정 - 긴급 가이드

## 📊 현재 상황 분석 (2024-01-19)

### 구현 상태 요약
- **전체 버튼**: 81개
- **완전 구현**: 25개 (31%)
- **부분 구현**: 7개 (9%)
- **미구현**: 49개 (60%)

---

## 🔴 즉시 조치 필요 - 충돌 방지

### A. 공통 컴포넌트 작업 규칙

#### 1. `DashboardActivity.navigateToFragment()`
```java
// ⚠️ 수정 전 팀원 간 협의 필수
// 이 메소드를 수정하면 모든 네비게이션에 영향

// 표준 호출 방식:
Fragment fragment = new TargetFragment();
Bundle args = new Bundle();
args.putString("user_role", userRole);
fragment.setArguments(args);
((DashboardActivity) requireActivity()).navigateToFragment(fragment);
```

#### 2. Bundle Arguments 표준화
```java
// 필수 key naming convention
public static final String KEY_USER_ROLE = "user_role";
public static final String KEY_PATIENT_NAME = "patient_name";
public static final String KEY_MANAGEMENT_MODE = "management_mode";
public static final String KEY_DETAIL_TYPE = "detail_type";
```

### B. 역할 기반 Fragment 처리
```java
// 모든 Fragment에서 표준 패턴 사용
private void getUserRole() {
    Bundle args = getArguments();
    if (args != null) {
        userRole = args.getString("user_role", "guardian");
    } else {
        userRole = "guardian"; // 기본값
    }
}
```

---

## 🎯 개별 작업 영역 분담 제안

### 영역 1: 채팅 시스템 (독립적)
**파일들**: 
- `fragment_chat.xml` 
- `ChatFragment.java`

**버튼 ID들**:
- `btn_attachment` - 파일 첨부
- `fab_send_message` - 메시지 전송
- `btn_quick_reply_*` - 빠른 답장

**기술 요구사항**:
- 실시간 메시징 (WebSocket/Firebase)
- 파일 업로드 서비스
- 채팅 내역 로컬 저장

---

### 영역 2: 공지사항 시스템 (일부 완료)
**파일들**:
- `fragment_notice_list.xml`
- `fragment_write_notice.xml` ✅ (UI 완성)
- `NoticeListFragment.java`
- `WriteNoticeFragment.java` ✅ (구조 완성)

**버튼 ID들**:
- `btn_write_notice` / `fab_write_notice`
- `btn_search`, `btn_load_more`
- `btn_save_draft`, `btn_publish`, `btn_preview`

**구현 필요**:
- 공지사항 CRUD API
- 검색 및 필터링
- 임시저장 기능

---

### 영역 3: 보호자 뉴스 허브 (독립적)
**파일들**:
- `fragment_guardian_news.xml`
- `GuardianNewsFragment.java`

**버튼 ID들** (6개 카드):
- `card_notice_list` - 공지사항
- `card_patient_photos` - 입소자 사진
- `card_health_updates` - 건강상태
- `card_activities` - 활동내역
- `card_meal_menu` - 식단표
- `card_events` - 행사소식

---

### 영역 4: 요양원 직원 환자 기록 (기본 구조 존재)
**파일들**:
- `fragment_caregiver_*.xml` (기본 UI 완성)
- `CaregiverMealRecordFragment.java`
- `CaregiverActivityRecordFragment.java`
- `CaregiverIndividualNoticeFragment.java`

**구현 필요**:
- 데이터 저장 로직
- 유효성 검증
- 사진 업로드 기능

---

## ⚡ 즉시 실행 항목

### 오늘 할 일 (2-3시간)
1. [ ] 각 팀원에게 담당 영역 배정
2. [ ] Bundle Arguments 표준 확정
3. [ ] 공통 컴포넌트 수정 규칙 공유
4. [ ] Git 브랜치 전략 확정

### 이번 주 할 일
1. [ ] API 스펙 1차 정의
2. [ ] 데이터 모델 클래스 작성
3. [ ] 공통 유틸리티 클래스 정의
4. [ ] 에러 처리 전략 수립

---

## 🔧 기술적 가이드라인

### 필수 권한 (AndroidManifest.xml)
```xml
<!-- 모든 영역에서 필요할 수 있는 권한들 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### 공통 응답 형식 (API)
```json
{
  "success": true,
  "message": "성공적으로 처리되었습니다",
  "data": { ... },
  "error": null
}
```

---

## 📞 소통 채널

### 긴급 이슈
- 공통 컴포넌트 수정 시 **반드시** 팀 전체에 알림
- Fragment 네이밍 변경 시 사전 협의

### 일일 체크인
- 오늘 작업한 버튼 ID
- 내일 작업 예정 버튼 ID
- 블로커 이슈 공유

---

*작성일: 2025-08-19*