# CodeRelief 프로젝트 구현 진행 상황

## 📌 **팀 협업 필수 문서**
**🎯 [Fragment별 버튼 기능 매핑](docs/FRAGMENT_BUTTON_MAPPING.md)** - 15개 Fragment, 58개 버튼의 상세 기능 정의 및 구현 현황 (2025-08-20 업데이트)

---

## 📋 프로젝트 개요
**CodeRelief**는 한국의 초고령사회(2024년 21.6% → 2030년 25.5% 노인인구) 대응을 위한 요양원 신뢰도 향상 시스템입니다. 요양원과 보호자 간의 소통 부족 문제를 해결하고 투명성을 높이는 Android 애플리케이션입니다.

### 프로젝트 비전 (안심:코드 팀)
- **문제 인식**: 요양원에 대한 보호자 불안과 불신 해소
- **핵심 가치**: 실시간 활동 공유, 건강 모니터링, 직접 소통을 통한 투명성 구현
- **타겟 사용자**: 50-60대 가족 보호자 (단순하고 접근하기 쉬운 인터페이스 설계)

### 주요 사용자 그룹
- **보호자 (Guardian)**: 가족의 요양원 생활 정보 실시간 조회 및 소통
- **요양원 직원 (Caregiver)**: 환자 정보 입력, 관리 및 보호자와의 소통

### 핵심 차별화 요소
- 기존 개별 기능 앱들과 달리 **통합 서비스** 제공
- 보호자 안심 서비스 + 요양원 업무 효율성 동시 개선
- 실시간 환자 모니터링 및 사진/활동 공유 기능

---

## 🎯 개발 방향
- **깡통 UI 디자인**: 기본적인 화면 전환과 네비게이션에 집중
- **Korean 언어**: 모든 UI 요소는 한국어로 구현
- **Material Design**: 일관된 디자인 시스템 적용
- **기술 스택 일관성**: Android Studio + Spring Boot + MySQL + AWS EC2
- **50-60대 친화적 UX**: 신체적 노화를 고려한 단순하고 직관적인 인터페이스

---

## 🏗️ 아키텍처 구조

### Application Flow
```
MainActivity (메인 화면)
├── 주변 요양원 찾기 → DashboardActivity → FindCareCenterFragment
└── 로그인/회원가입 → LoginActivity → DashboardActivity
    ├── Guardian Role → GuardianMainFragment
    └── Caregiver Role → CaregiverMainFragment
```

### Fragment-Based Navigation
- **DashboardActivity**: 중앙 네비게이션 허브
- **Role-based UI**: 사용자 역할에 따른 서로 다른 인터페이스 제공
- **Fragment Arguments**: Bundle을 통한 데이터 전달

---

## ✅ 완료된 구현 사항

### 1. 기본 구조 설정
- [x] MainActivity 레이아웃 수정 (2버튼 구조)
- [x] LoginActivity 간소화 (역할 기반 로그인)
- [x] DashboardActivity 네비게이션 시스템
- [x] 기본 Fragment들 구조 정의

### 2. 보호자 (Guardian) 화면 구현

**GuardianMainFragment**
- [x] 4개 메뉴 버튼: 내가족보기, 소식메뉴, 요양원과 채팅, 일정확인

**PatientListFragment (역할별 구분 동적 관리)**
- [x] **보호자용**: FamilyMemberAdapter + item_family_member.xml (가족 용어 사용)
- [x] **직원용**: PatientAdapter + item_patient.xml (수급자 용어 사용)
- [x] **스키마 기반 Patient 모델 클래스**
- [x] 무제한 환자/가족 지원 (하드코딩 제거)
- [x] **보호자 버튼**: '자세히 보기', '요양원과 채팅'
- [x] **직원 버튼**: '상세정보', '보호자 연락'
- [x] 요양원 정보 표시 (보호자용 - 여러 요양원 고려)

**PatientDetailFragment (보호자용 정보 조회)**
- [x] 급여 정보 표시 (식사현황, 투약현황, 건강상태)
- [x] 활동 정보 표시 (오전/오후/저녁 활동 내역)
- [x] detail_type 파라미터로 급여/활동 구분
- [x] 깔끔한 카드 기반 레이아웃

**GuardianPatientDetailFragment (보호자 전용 가족 상세)**
- [x] **coderelief1 GuardianPatientDetailScreenPreview 기반** 구현
- [x] 가족 기본 정보 (이름, 나이, 방호실, 요양등급, 입소일, 관계)
- [x] **입소 요양원 상세 정보** (요양원명, 주소, 연락처, 담당간호사)
- [x] **2개 주요 버튼**: 🍽️ 오늘의 급여 확인, 🎯 오늘의 활동 확인
- [x] PatientDetailFragment로 연결 (meal/activity 타입별)

### 3. 요양원 직원 (Caregiver) 화면 구현

**CaregiverMainFragment**
- [x] 4개 메뉴 버튼: 수급자 리스트, 공지사항, 면담/면회/상담 일정, 일정 관리
- [x] 공지사항 버튼: WriteNoticeFragment로 직접 연결 (효율적인 워크플로우)

**PatientListFragment (직원용 - 동일한 RecyclerView 구조)**
- [x] 역할 기반 UI 동일 (Guardian/Caregiver 공용)
- [x] 동적 환자 목록으로 확장성 확보
- [x] 스키마 기반 데이터 처리로 API 연동 준비 완료

**CaregiverPatientDetailFragment**
- [x] 환자 기본 정보 표시 (나이, 방호실, 요양등급, 입소일, 보호자 정보)
- [x] 3개 입력 메뉴: 급여 내역 입력, 활동 프로그램 입력, 개별 공지 작성
- [x] 각 메뉴별 적절한 색상 구분

**CaregiverMealRecordFragment**
- [x] 식사 현황 입력 (아침, 점심, 저녁)
- [x] 투약 현황 입력 (복용 약물 및 시간)
- [x] 건강상태 입력 (체온, 혈압, 혈당)
- [x] 특이사항 입력
- [x] 필수 필드 유효성 검사
- [x] 저장 완료 시 이전 화면으로 복귀

**CaregiverActivityRecordFragment**
- [x] 활동 정보 입력 (제목, 시간, 내용, 참여도)
- [x] 사진 업로드 영역 (촬영/갤러리 선택)
- [x] 사진 설명 입력
- [x] 저장 기능 및 네비게이션

**CaregiverIndividualNoticeFragment**
- [x] 우선순위 선택 (긴급/중요/일반)
- [x] 우선순위별 버튼 색상 변경
- [x] 공지 제목/내용 입력
- [x] 보호자 전용 공지 전송 기능

### 4. 공통 기능
**ScheduleFragment**
- [x] 역할별 접근 권한 (보호자: 조회, 직원: 관리)
- [x] management_mode 파라미터로 기능 구분
- [x] 캘린더 기반 일정 관리
- [x] 대기 중인 신청 표시 (직원용)

**ChatFragment**
- [x] 기본 채팅 UI 구조 (header, 메시지 영역, 입력 영역)
- [x] 역할 기반 채팅방 설정 (guardian/caregiver/care_center)
- [x] 실시간 메시징 준비 (WebSocket 연동 대기)
- [x] 파일 첨부 기능 준비
- [x] ~~빠른 답장 기능 제거~~ (사용자 피드백 반영)

---

## 📁 주요 파일 구조

### Java 구조
```
com.example.coderelief/
├── fragments/
│   ├── GuardianMainFragment.java          // 보호자 메인 메뉴
│   ├── CaregiverMainFragment.java         // 직원 메인 메뉴
│   ├── PatientListFragment.java          // 동적 환자 목록 (RecyclerView)
│   ├── FindCareCenterFragment.java       // 동적 요양원 검색 (RecyclerView)
│   ├── PatientDetailFragment.java        // 환자 정보 조회
│   ├── CaregiverPatientDetailFragment.java    // 직원용 환자 관리 (4개 버튼)
│   ├── NewsWriteFragment.java            // 소식 작성 (coderelief1 기반)
│   ├── CaregiverMealRecordFragment.java       // 급여 내역 입력
│   ├── CaregiverActivityRecordFragment.java   // 활동 프로그램 입력
│   ├── CaregiverIndividualNoticeFragment.java // 개별 공지 작성
│   ├── ScheduleFragment.java             // 일정 관리
│   ├── ChatFragment.java                 // 채팅 기능
│   └── ConsultationFragment.java         // 상담 신청
├── models/
│   ├── Patient.java                      // 스키마 기반 환자 모델
│   ├── Institution.java                  // 스키마 기반 요양원 모델
│   └── CareCenter.java                   // 요양원 검색용 모델
├── adapters/
│   ├── PatientAdapter.java               // 환자 목록 RecyclerView 어댑터
│   ├── CareCenterAdapter.java            // 요양원 목록 RecyclerView 어댑터
│   └── CalendarAdapter.java              // 캘린더 어댑터
└── 기타 클래스들...
```

### Layout Files
```
res/layout/
├── activity_main.xml                 // 메인 화면 (2버튼)
├── fragment_guardian_main.xml        // 보호자 메인 메뉴
├── fragment_caregiver_main.xml       // 직원 메인 메뉴
├── fragment_patient_list.xml         // 동적 환자 목록 (RecyclerView)
├── fragment_find_care_center.xml     // 동적 요양원 검색 (RecyclerView + 지도)
├── item_patient.xml                  // 환자 카드 아이템 레이아웃 (상세정보 + 보호자연락)
├── item_care_center.xml              // 요양원 카드 아이템 레이아웃
├── fragment_patient_detail.xml       // 환자 정보 조회
├── fragment_caregiver_patient_detail.xml      // 직원용 환자 관리 (4개 버튼)
├── fragment_news_write.xml           // 소식 작성 폼 (제목, 내용, 사진첨부)
├── fragment_caregiver_meal_record.xml         // 급여 내역 입력 폼
├── fragment_caregiver_activity_record.xml     // 활동 프로그램 입력 폼
├── fragment_caregiver_individual_notice.xml   // 개별 공지 작성 폼
└── 기타 Layout들...
```

---

## 🎨 디자인 시스템

### Color Scheme
- **Primary Purple**: `@color/purple_500` - 주요 기능 버튼
- **Secondary Teal**: `@color/teal_700` - 보조 기능 버튼  
- **Dark Purple**: `@color/purple_700` - 강조 기능 버튼
- **Error Red**: `android.R.color.holo_red_dark` - 긴급 알림

### UI Components
- **MaterialCardView**: 정보 표시 카드
- **TextInputLayout**: 일관된 입력 필드
- **Button**: Material Design 3 스타일
- **ScrollView**: 긴 내용의 스크롤 지원

---

## 🔄 사용자 플로우

### 보호자 플로우 (단순화 완료)
```
로그인 (Guardian) → GuardianMainFragment (3개 메뉴)
├── 우리 가족 → PatientListFragment (보호자용 - FamilyMemberAdapter)
│   └── 가족 선택 → GuardianPatientDetailFragment (통합 강화)
│       ├── 📸 최근 사진 (가족별 활동 사진)
│       ├── 📢 가족 전용 소식 (개별 공지사항)
│       ├── 오늘의 급여 확인 → PatientDetailFragment(meal)
│       └── 오늘의 활동 확인 → PatientDetailFragment(activity)
│   └── 요양원과 채팅 → ChatFragment (care_center 타입)
├── 채팅하기 → 스마트 가족별 채팅 (1인: 직접, 2인: 선택, 3인+: Dialog)
└── 일정 보기 → ScheduleFragment (읽기 전용)
```

### 요양원 직원 플로우 (기존 유지)
```
로그인 (Caregiver) → CaregiverMainFragment
├── 수급자 리스트 → PatientListFragment (직원용 - PatientAdapter)
│   └── 환자 선택 → CaregiverPatientDetailFragment
│       ├── 급여 내역 입력 → CaregiverMealRecordFragment
│       ├── 활동 프로그램 입력 → CaregiverActivityRecordFragment
│       ├── 소식 입력 → NewsWriteFragment
│       └── 개별 공지 작성 → CaregiverIndividualNoticeFragment
│   └── 보호자 연락 → ChatFragment (guardian 타입)
├── 공지사항 → WriteNoticeFragment (직접 공지사항 작성)
├── 면담/면회/상담 일정 → ScheduleFragment
└── 일정 관리 → ScheduleFragment (관리 모드)
```

### 역할별 UI 분리 특징 (NEW)
```
보호자 (Guardian):
 - 용어: '가족' 사용, '자세히 보기', '요양원과 채팅'
 - 요양원 정보 표시 (여러 요양원 고려)
 - 전용 GuardianPatientDetailFragment
 
직원 (Caregiver):
 - 용어: '수급자', '상세정보', '보호자 연락'
 - 기존 CaregiverPatientDetailFragment 유지
 - 소식 작성 기능 포함
```

---

## ⚙️ 기술적 구현 사항

### Navigation Pattern
- **Intent-based**: Activity 간 이동
- **Fragment Transaction**: Activity 내 Fragment 교체
- **Bundle Arguments**: 데이터 전달
- **Back Stack**: 네비게이션 히스토리 관리

### Data Handling
- **Role-based Access**: `user_role` 파라미터 활용
- **Patient Context**: `patient_name` 기반 환자별 기능
- **Validation**: 필수 입력 필드 검증
- **Toast Feedback**: 사용자 액션에 대한 즉시 피드백

### Code Organization
- **Fragment per Screen**: 화면별 Fragment 분리
- **Shared Layouts**: 공통 디자인 패턴 재사용
- **Consistent Naming**: 명확한 네이밍 컨벤션
- **Korean Comments**: 한국어 주석으로 가독성 향상

---

## 🚧 미완성/예정 작업

### 📋 팀 작업 계획 및 버튼 기능 매핑 (진행 중)
- [x] ~~프로젝트 배경 지식 분석 완료~~ (PDF 문서 기반)
- [x] ~~기술 스택별 버튼 기능 매핑 문서 작성~~ (58개 버튼 분석)
- [x] ~~데이터베이스 스키마 설계~~ (7개 테이블, 조원 협의 완료)
- [x] ~~NewsWriteFragment 구현 완료~~ (coderelief1 WriteNewsScreenPreview 기반)
- [ ] **GuardianChatSelectionFragment RecyclerView 최적화** - 불필요한 UI 요소 제거
- [ ] **채팅 선택용 전용 어댑터 및 레이아웃 생성 고려** (간소화된 UI)
- [ ] **팀원 구현 UI 파일 분석 및 실제 버튼 ID 매핑** (대기 중)
- [ ] 채팅 시스템용 테이블 추가 (chat_room, chat_message)
- [ ] Spring Boot API 엔드포인트 설계
- [ ] 팀원별 버튼 기능 구현 시작

### 단기 작업 (UI 구조 완성)
- [x] ~~NewsWriteFragment 구현~~ (소식 작성 화면 완료)
- [ ] FindCareCenterFragment 상세 구현 (지도 영역 팀원 작업)
- [ ] GuardianNewsFragment 뉴스 피드 구현
- [ ] NoticeListFragment 공지사항 목록

### 중기 작업 (기능 개선)
- [ ] MySQL 데이터베이스 연동 (7개 기본 테이블 + 채팅 테이블)
- [ ] Spring Boot REST API 구축
- [ ] AWS S3 이미지 업로드 구현
- [ ] Spring Boot WebSocket + STOMP 실시간 채팅
- [ ] Room Database 로컬 데이터 캐싱
- [ ] Glide 이미지 로딩 및 압축
- [ ] 사용자 인증 시스템 (JWT)

### 장기 작업 (고도화)
- [ ] 푸시 알림 시스템
- [ ] 다국어 지원
- [ ] 접근성 개선
- [ ] 성능 최적화
- [ ] 보안 강화

---

## 📝 개발 노트

### 주요 결정사항
1. **깡통 UI 접근**: 복잡한 비즈니스 로직 대신 화면 전환에 집중
2. **역할 기반 UI**: 같은 화면도 사용자 역할에 따라 다른 동작
3. **참고 프로젝트 활용**: codereleif1의 Compose UI를 Android View로 변환
4. **한국어 우선**: 모든 텍스트를 한국어로 구현

### 코딩 컨벤션
- **파일명**: PascalCase (Fragment, Activity)
- **변수명**: camelCase  
- **리소스 ID**: snake_case (btn_patient_list)
- **색상**: semantic naming (@color/purple_500)

### 버전 관리
- **Target SDK**: 35
- **Min SDK**: 24
- **Gradle**: 8.12.0
- **Material Design**: 3.x

---

## 🏁 현재 상태

### ✅ 완료된 화면
- 메인 화면 (MainActivity)
- 로그인 화면 (LoginActivity) 
- 보호자 메인 메뉴 (GuardianMainFragment)
- 요양원 직원 메인 메뉴 (CaregiverMainFragment)
- 환자 목록 (PatientListFragment) - 역할별 기능 분화
- 환자 정보 조회 (PatientDetailFragment)
- 직원용 환자 관리 (CaregiverPatientDetailFragment)
- 급여 내역 입력 (CaregiverMealRecordFragment)
- 활동 프로그램 입력 (CaregiverActivityRecordFragment)
- 개별 공지 작성 (CaregiverIndividualNoticeFragment)
- 일정 관리 (ScheduleFragment)

### 🔧 현재 작업 가능한 플로우
1. **메인 → 로그인 → 역할별 메인 메뉴** ✅
2. **보호자: 내가족보기 → 동적 가족 목록 → 가족 상세정보/요양원채팅** ✅
3. **직원: 수급자리스트 → 동적 환자 목록 → 환자상세관리 → 소식작성/급여기록/활동기록** ✅
6. **보호자 전용: 가족 상세정보 → 오늘의 급여/활동 확인** ✅
4. **요양원 찾기 → 동적 요양원 목록 → 상세정보** ✅
5. **일정 관리 (역할별 권한 차별화)** ✅

### 📊 진행률
- **UI 구조**: 99% 완성 (동적 구조 + 역할별 UI 분리 완료)
- **기본 네비게이션**: 99% 완성 (보호자/직원 전용 플로우)
- **동적 데이터 처리**: 90% 완성 (역할별 어댑터 + 스키마 기반 모델)
- **팀 협업 체계**: 97% 완성 (문서화, 스키마 매핑, UI 분리 완료)
- **데이터 연동 준비**: 88% (스키마 기반 모델 + 역할별 데이터 처리)
- **핵심 기능 구현**: 80% (동적 관리 + 소식 작성 + 보호자 UI)
- **API 연동 준비**: 78% (Repository 패턴 대기, 역할별 구조 완성)

---

## 📈 최근 변경 사항

### v4.3: 보호자 메뉴 단순화 및 기능 통합 최적화 (2025-08-20)
**주요 업데이트**:
- [x] **보호자 메뉴 단순화**: 4개 → 3개 메뉴로 변경 (소식 메뉴 제거)
- [x] **GuardianPatientDetailFragment 기능 통합**: 기존 소식 메뉴 기능을 가족별로 통합 제공
- [x] **📸 최근 사진 섹션 추가**: 가족별 활동 사진 목록 제공
- [x] **📢 가족 전용 소식 섹션 추가**: 개별 공지사항, 처방 변경, 면회 일정 등 제공
- [x] **메뉴 버튼명 개선**: '내가족보기' → '우리 가족', '요양원과 채팅' → '채팅하기'
- [x] **중복 기능 제거**: 소식 메뉴와 가족 상세의 기능 중복 해결
- [x] **사용자 혼동 방지**: 명확한 정보 접근 경로 제공

**삭제된 파일**:
- GuardianNewsFragment 사용 중단 (기능은 GuardianPatientDetailFragment에 통합)

**수정된 파일**:
- `layout/fragment_guardian_main.xml` - 3개 메뉴로 단순화
- `fragments/GuardianMainFragment.java` - btn_news_menu 관련 코드 제거
- `layout/fragment_guardian_patient_detail.xml` - 사진/공지사항 섹션 추가
- `fragments/GuardianPatientDetailFragment.java` - 새로운 섹션 데이터 로딩 로직 추가

### v4.2: 보호자/직원 UI 완전 분리 및 역할별 최적화 (2025-08-20)
**주요 업데이트**:
- [x] **보호자 전용 UI 구현**: GuardianPatientDetailFragment + FamilyMemberAdapter
- [x] **용어 체계 분리**: '환자' vs '가족', '상세정보' vs '자세히 보기'
- [x] **요양원 정보 표시**: 보호자가 여러 요양원에 가족 입소 경우 고려
- [x] **coderelief1 GuardianPatientDetailScreenPreview 기반** 보호자 전용 상세 화면
- [x] **역할별 네비게이션 분기**: PatientListFragment에서 역할 인식 후 다른 어댑터 사용
- [x] **채팅 타입 구분**: care_center vs guardian 타입으로 채팅상대 구분
- [x] **2개 주요 버튼**: 오늘의 급여/활동 확인 버튼으로 PatientDetailFragment 연결

**새로 생성된 파일**:
- `layout/item_family_member.xml` - 보호자용 가족 아이템 (요양원 정보 포함)
- `layout/fragment_guardian_patient_detail.xml` - 보호자 전용 가족 상세 화면
- `fragments/GuardianPatientDetailFragment.java` - coderelief1 기반 보호자 전용 Fragment
- `adapters/FamilyMemberAdapter.java` - 보호자용 가족 목록 어댑터

**수정된 파일**:
- `fragments/PatientListFragment.java` - 역할별 어댑터 분기 로직 추가

### v4.1: NewsWriteFragment 구현 및 UI 플로우 완성 (2025-08-20)
**주요 업데이트**:
- [x] **NewsWriteFragment 완전 구현** (coderelief1 WriteNewsScreenPreview 기반)
- [x] **완전한 소식 작성 플로우**: 환자 목록 → 상세정보 → 소식 작성 → 게시/임시저장
- [x] **UI 플로우 재구성**: coderelief1 staffscreens.kt 패턴 적용
- [x] **환자별 소식 작성**: patient_id 전달 및 환자 정보 표시
- [x] **Material Design 완전 적용**: TextInputLayout, MaterialCardView, 일관된 색상
- [x] **유효성 검사**: 제목/내용 필수 입력 검증
- [x] **사진 첨부 영역**: 갤러리/카메라 선택 준비 (UI 완료)
- [x] **임시저장/게시 기능**: 기본 로직 구현 (API 연동 대기)

**새로 생성된 파일**:
- `fragments/NewsWriteFragment.java`, `layout/fragment_news_write.xml`

**수정된 파일**:
- `CaregiverPatientDetailFragment.java` - NewsWriteFragment 연결 및 patient_id 전달
- `docs/FRAGMENT_BUTTON_MAPPING.md` - NewsWriteFragment 추가 및 동적 구조 반영

### v4: 동적 UI 구조 리팩토링 및 스키마 기반 모델 구현 (2025-08-20)
**주요 업데이트**:
- [x] **PatientListFragment → RecyclerView 기반으로 완전 리팩토링**
- [x] **FindCareCenterFragment → RecyclerView 기반으로 완전 리팩토링** (지도 영역 보존)
- [x] **스키마 기반 모델 클래스 구현**: Patient, Institution, CareCenter
- [x] **동적 어댑터 구현**: PatientAdapter, CareCenterAdapter
- [x] **아이템 레이아웃 생성**: item_patient.xml, item_care_center.xml
- [x] **하드코딩 제거**: 환자1/환자2/환자3, 요양원1/요양원2/요양원3 버튼 삭제
- [x] **확장성 확보**: 무제한 환자/요양원 지원
- [x] **API 연동 준비**: 스키마 기반 데이터 처리 구조 완성

**새로 생성된 파일**:
- `models/Patient.java`, `models/Institution.java`, `models/CareCenter.java`
- `adapters/PatientAdapter.java`, `adapters/CareCenterAdapter.java`
- `layout/item_patient.xml`, `layout/item_care_center.xml`

**수정된 파일**:
- `PatientListFragment.java` - RecyclerView 기반으로 완전 재작성
- `FindCareCenterFragment.java` - RecyclerView 기반으로 리팩토링
- `fragment_patient_list.xml`, `fragment_find_care_center.xml` - 동적 구조로 변경

### v3: 프로젝트 배경 지식 통합 및 팀 협업 체계 구축 (2025-08-19)
**주요 업데이트**:
- [x] **배경 지식 통합**: 안심:코드 팀 원본 제안서 및 발표자료 분석 완료
- [x] **기술 스택 표준화**: Android Studio + Spring Boot + MySQL + AWS 일관성 확보
- [x] **버튼 기능 매핑**: 58개 버튼 분석 및 4개 팀원별 담당 영역 분배
- [x] **데이터베이스 설계**: 7개 기본 테이블 스키마 (조원 협의 완료)
- [x] **팀 협업 문서**: 충돌 방지 가이드라인 및 표준화 규칙 수립
- [x] **채팅 UI 개선**: 빠른 답장 버튼 제거 (사용자 피드백 반영)

**새로 생성된 문서**:
- `docs/FRAGMENT_BUTTON_MAPPING.md` - 58개 버튼 기능 상세 매핑
- `docs/BUTTON_ASSIGNMENTS.md` - 팀원별 버튼 구현 배정표
- `docs/TEAM_COORDINATION_URGENT.md` - 팀 작업 조정 가이드
- `docs/DATABASE_SCHEMA.md` - DB 스키마 및 버튼 매핑

### v2: Caregiver 공지사항 네비게이션 개선
**변경 내용**: `CaregiverMainFragment.java:52-54`
- **이전**: 공지사항 버튼 → `NoticeListFragment` (공지사항 목록 조회)
- **현재**: 공지사항 버튼 → `WriteNoticeFragment` (공지사항 직접 작성)

**배경**: codeRelef1 프로젝트의 `WriteNoticeScreenPreview` 패턴을 참조하여 caregiver가 공지사항을 클릭할 때 바로 작성 화면으로 이동하도록 수정. 보호자는 여전히 공지사항 조회만 가능하며, 직원은 공지사항 작성에 직접 접근 가능.

**영향받는 파일**:
- `CaregiverMainFragment.java` - 네비게이션 로직 수정
- `CLAUDE.md` - 아키텍처 문서 업데이트
- `IMPLEMENTATION_PROGRESS.md` - 구현 현황 문서 업데이트

---

---

## 📚 프로젝트 문서 구조

### 📁 docs/ 폴더
- `FRAGMENT_BUTTON_MAPPING.md` - 15개 Fragment별 58개 버튼 상세 매핑
- `BUTTON_ASSIGNMENTS.md` - 4개 팀원별 버튼 구현 배정 및 기술 요구사항
- `DATABASE_SCHEMA.md` - MySQL 스키마 설계 및 버튼-테이블 매핑
- `TEAM_COORDINATION_URGENT.md` - 팀 작업 충돌 방지 가이드라인

### 📁 backgroundknowledge/ 폴더 (gitignore)
- `발표자료.pdf` - 안심:코드 팀 프로젝트 발표자료
- `개발제안서.pdf` - 상세 프로젝트 제안서
- `projectTable.txt` - 데이터베이스 테이블 스키마 (조원 협의)

### 🎯 다음 단계
1. **팀원 UI 파일 분석**: 실제 구현된 버튼 ID로 매핑 업데이트 예정
2. **Spring Boot API 설계**: RESTful 엔드포인트 정의
3. **채팅 테이블 추가**: chat_room, chat_message 스키마 협의
4. **개별 기능 구현 시작**: 팀원별 담당 영역 개발 착수

---

*마지막 업데이트: 2025년 8월 20일*  
*프로젝트 경로: C:\CodeRelief*  
*프로젝트: 안심:코드 팀 (초고령사회 대응 요양원 신뢰도 향상 시스템)*  
*기술 스택: Android Studio (Java) + Spring Boot + MySQL + AWS EC2*  
*역할별 UI 분리: Guardian(보호자) vs Caregiver(직원) 전용 인터페이스*
