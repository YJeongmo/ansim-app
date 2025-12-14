# 🎯 버튼 기능 구현 배정표

## 📋 작업 배정 현황

### 👤 서이 - 로그인 & 공지사항 시스템
**담당 파일**: `LoginActivity.java`, `NoticeListFragment.java`, `WriteNoticeFragment.java`

| Button ID | 설명 | 우선순위 | 예상공수 | 상태 |
|-----------|------|----------|----------|------|
| **로그인 시스템** |
| `btn_login` | 사용자 인증 (Guardian/Caregiver) | HIGH | 2일 | 🔄 진행중 |
| **공지사항 관리** |
| `btn_publish` | 공지사항 게시 | HIGH | 1일 | 🔄 진행중 |
| `btn_save_draft` | 임시저장 | HIGH | 1일 | ⏳ 대기 |
| `btn_preview` | 미리보기 | MEDIUM | 1일 | ⏳ 대기 |
| `btn_search` | 공지사항 검색 | MEDIUM | 1일 | ⏳ 대기 |
| `btn_load_more` | 더 많은 공지 로드 | LOW | 0.5일 | ⏳ 대기 |

**기술 요구사항**:
- Spring Boot Security (JWT 토큰 인증)
- Spring Boot REST API (공지사항 CRUD)
- MySQL Full-text search (공지사항 검색)
- Room Database (임시저장 관리)

---

### 👤 결 - UI 최적화 (목요일까지)
**담당 파일**: 전체 Fragment 레이아웃 최적화

| 작업 영역 | 설명 | 우선순위 | 예상공수 | 상태 |
|-----------|------|----------|----------|------|
| **GuardianChatSelectionFragment** | RecyclerView 최적화, 불필요 UI 요소 제거 | HIGH | 1일 | 🔄 진행중 |
| **채팅 선택용 전용 어댑터** | 간소화된 UI 어댑터 생성 검토 | MEDIUM | 1일 | ⏳ 대기 |
| **Material Design 통일화** | 전체 Fragment Purple 헤더 패턴 표준화 | MEDIUM | 1일 | ⏳ 대기 |
| **환자 사진 영역 정리** | 점선 테두리 FrameLayout 일관성 | LOW | 0.5일 | ⏳ 대기 |

**기술 요구사항**:
- Material Design Components 최적화
- RecyclerView 성능 튜닝
- UI/UX 일관성 확보

---

### 👤 준호 - 지도 & 요양원 데이터 관리
**담당 파일**: `FindCareCenterFragment.java`, `CareCenterInfoFragment.java`

| Button ID | 설명 | 우선순위 | 예상공수 | 상태 |
|-----------|------|----------|----------|------|
| **지도 기능** |
| `지도 영역 구현` | 요양원 위치 표시 지도 | HIGH | 2일 | 🔄 진행중 |
| **요양원 데이터** |
| `btn_care_center_detail` | 요양원 상세정보 | HIGH | 1일 | ⏳ 대기 |
| `btn_consultation` | 상담 예약 연결 | MEDIUM | 1일 | ⏳ 대기 |
| **데이터 관리** |
| `CareCenterAdapter 확장` | 동적 요양원 데이터 로딩 | MEDIUM | 1일 | ⏳ 대기 |

**기술 요구사항**:
- Google Maps API 또는 네이버 지도 API
- Spring Boot REST API (요양원 데이터)
- GPS 위치 권한 처리
- RecyclerView 데이터 바인딩

---

### 👤 정모 - 환자 정보 기록 및 열람
**담당 파일**: `CaregiverMealRecordFragment.java`, `CaregiverActivityRecordFragment.java`, `PatientDetailFragment.java`

| Button ID | 설명 | 우선순위 | 예상공수 | 상태 |
|-----------|------|----------|----------|------|
| **환자 기록 관리** |
| `btn_save` (meal) | 급여 내역 저장 | HIGH | 1일 | 🔄 진행중 |
| `btn_save` (activity) | 활동 기록 저장 | HIGH | 1일 | ⏳ 대기 |
| `btn_photo_capture` | 사진 촬영 및 업로드 | HIGH | 1일 | ⏳ 대기 |
| `btn_photo_gallery` | 갤러리 선택 | MEDIUM | 0.5일 | ⏳ 대기 |
| **환자 정보 열람** |
| `환자 상세정보 표시` | Patient 모델 데이터 바인딩 | MEDIUM | 1일 | ⏳ 대기 |
| `btn_send` | 개별 공지 전송 | LOW | 1일 | ⏳ 대기 |

**기술 요구사항**:
- Android Camera API + 권한 처리
- Glide 이미지 압축 및 변환
- Spring Boot REST API (환자 데이터 CRUD)
- Room Database (로컬 데이터 지속성)

---

## 🔄 일일 체크인 템플릿

### 오늘의 진행상황 (날짜: _______)

**서이 (로그인 & 공지사항)**:
- ✅ 완료: 
- 🔄 진행중: 
- ⚠️ 블로커: 
- 📅 내일 계획: 

**결 (UI 최적화)**:
- ✅ 완료: 
- 🔄 진행중: 
- ⚠️ 블로커: 
- 📅 목요일까지 완료 목표: GuardianChatSelectionFragment 최적화

**준호 (지도 & 요양원 데이터)**:
- ✅ 완료: 
- 🔄 진행중: 
- ⚠️ 블로커: 
- 📅 내일 계획: 

**정모 (환자 정보 기록/열람)**:
- ✅ 완료: 
- 🔄 진행중: 
- ⚠️ 블로커: 
- 📅 내일 계획: 

---

## 🚨 공통 이슈 추적

### 해결 필요한 공통 이슈
- [ ] Spring Boot API 엔드포인트 확정 (RESTful 설계)
- [x] ~~MySQL 데이터베이스 스키마 설계~~ (조원 협의 완료 - projectTable.txt 참조)
- [ ] 채팅 시스템 테이블 추가 필요 (chat_room, chat_message)
- [ ] 공통 유틸리티 클래스 작성 (NetworkUtils, ImageUtils 등)
- [ ] 공통 에러 처리 (GlobalExceptionHandler + 토스트 메시지)

### 의존성 충돌 방지
- [ ] DashboardActivity.navigateToFragment() 표준화
- [ ] Bundle arguments key 통일 (KEY_USER_ROLE, KEY_PATIENT_NAME 등)
- [ ] Constants 클래스 작성 (API endpoints, Fragment keys)
- [ ] Git feature branch 전략 수립 (feature/button-functionality 패턴)

---