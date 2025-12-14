# 🎯 Fragment별 버튼 기능 매핑

## 📋 굵직한 Fragment별 버튼 정리

---

## 🏠 **메인 네비게이션**

### 1. **GuardianMainFragment** (단순화 완료)
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_patient_list` | 우리 가족 → PatientListFragment (통합 강화) | ✅ 완료 |
| `btn_chat` | 채팅하기 → GuardianChatSelectionFragment (가족별 채팅 선택) | ✅ 완료 |
| `btn_schedule` | 일정 보기 → ScheduleFragment | ✅ 완료 |

**메뉴 단순화**: 4개 → 3개 메뉴로 변경, '소식 메뉴' 기능을 '우리 가족'에 통합
**채팅 시스템 개선**: 스마트바 → 전용 선택 화면으로 변경

### 2. **CaregiverMainFragment**
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_recipient_list` | 수급자 리스트 → PatientListFragment(caregiver) | ✅ 완료 |
| `btn_notices` | 공지사항 → WriteNoticeFragment | ✅ 완료 |
| `btn_consultation_schedule` | 면담/면회/상담 일정 → ScheduleFragment | ✅ 완료 |
| `btn_schedule_management` | 일정 관리 → ScheduleFragment(management) | ✅ 완료 |

---

## 📰 **정보 관리**

### 3. **GuardianNewsFragment** - 🗑️ 기능 통합으로 사용 중단
**변경사항**: 기존 소식 메뉴의 모든 기능이 GuardianPatientDetailFragment에 통합됨
- `card_patient_photos` → 📸 최근 사진 섹션으로 통합
- `card_notice_list` → 📢 가족 전용 소식 섹션으로 통합  
- `card_health_updates`, `card_activities` → 기존 "오늘의 급여/활동 확인" 버튼 활용
- `card_meal_menu`, `card_events` → 가족 전용 소식에 포함

**장점**: 가족별 구분된 정보 제공, 중복 기능 제거, 사용자 혼동 방지

### 4. **NoticeListFragment** - 공지사항 목록
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_write_notice` | 공지사항 작성 | ❌ 구현필요 |
| `fab_write_notice` | 공지사항 작성 (FAB) | ❌ 구현필요 |
| `btn_search` | 공지사항 검색 | ❌ 구현필요 |
| `btn_load_more` | 더 많은 공지 로드 | ❌ 구현필요 |

### 5. **WriteNoticeFragment** - 공지사항 작성
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_save_draft` | 임시저장 | ❌ 구현필요 |
| `btn_attach_image` | 이미지 첨부 | ❌ 구현필요 |
| `btn_attach_file` | 파일 첨부 | ❌ 구현필요 |
| `btn_preview` | 미리보기 | ❌ 구현필요 |
| `btn_publish` | 게시하기 | ❌ 구현필요 |

---

## 💬 **소통**

### 6. **GuardianChatSelectionFragment** - 가족별 채팅 선택 화면 (신규 추가)
| Component | 기능 | 구현상태 |
|-----------|------|----------|
| `RecyclerView rv_family_chat_list` | 가족 목록 표시 (FamilyMemberAdapter 재사용) | ✅ 완료 |
| `FamilyMemberAdapter.OnFamilyActionListener` | 가족 선택 시 해당 요양원과 채팅 연결 | ✅ 완료 |
| **레이아웃 디자인 통일화** | Purple 헤더, 중앙 정렬 설명 텍스트 | ✅ 완료 |
| **데이터 일관성** | PatientListFragment와 동일한 가족 데이터 공유 | ✅ 완료 |

### 7. **ChatFragment** - 채팅 시스템
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_attachment` | 파일 첨부 | ❌ 구현필요 |
| `fab_send_message` | 메시지 전송 | ❌ 구현필요 |
| **chat_type 파라미터** | care_center/guardian 타입 구분 채팅 | ✅ 완료 |

---

## 📅 **일정 관리**

### 7. **ScheduleFragment** - 캘린더
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_prev_month` | 이전 달 | ✅ 완료 |
| `btn_next_month` | 다음 달 | ✅ 완료 |
| `btn_add_schedule` | 일정 추가 | ❌ 구현필요 |
| `btn_quick_medical` | 진료 일정 추가 | ❌ 구현필요 |
| `btn_quick_therapy` | 물리치료 일정 추가 | ❌ 구현필요 |
| `btn_quick_activity` | 활동프로그램 일정 추가 | ❌ 구현필요 |
| `btn_quick_family_visit` | 가족면회 일정 추가 | ❌ 구현필요 |

---

## 👥 **환자 관리**

### 8. **PatientListFragment** - 역할별 구분 목록 (RecyclerView 기반)

#### 8-1. **보호자용 (Guardian)** - 가족 목록
| Component | 기능 | 구현상태 |
|-----------|------|----------|
| `RecyclerView recycler_view_patients` | 동적 가족 목록 표시 | ✅ 완료 |
| `FamilyMemberAdapter` | 가족 데이터 바인딩 (보호자 전용) | ✅ 완료 |
| `item_family_member.xml` | 가족 카드 아이템 레이아웃 (요양원 정보 포함) | ✅ 완료 |
| `frame_patient_photo` (아이템 내) | 환자 사진 영역 (80x80dp, 점선 테두리) | ✅ 완료 |
| `btn_family_detail` (아이템 내) | 가족 상세정보 '자세히 보기' | ✅ 완료 |
| `btn_chat_with_center` (아이템 내) | '요양원과 채팅' 버튼 | ✅ 완료 |

#### 8-2. **직원용 (Caregiver)** - 수급자 목록
| Component | 기능 | 구현상태 |
|-----------|------|----------|
| `RecyclerView recycler_view_patients` | 동적 환자 목록 표시 | ✅ 완료 |
| `PatientAdapter` | 환자 데이터 바인딩 | ✅ 완료 |
| `Patient 모델 클래스` | 스키마 기반 환자 정보 | ✅ 완료 |
| `item_patient.xml` | 환자 카드 아이템 레이아웃 | ✅ 완료 |
| `btn_patient_detail` (아이템 내) | 환자 상세정보 화면 이동 | ✅ 완료 |
| `btn_contact_guardian` (아이템 내) | 선택된 환자 보호자 연락 | ✅ 완료 |
| **UI 정리** | 불필요한 요양등급 배지 제거 | ✅ 완료 |

### 9. **GuardianPatientDetailFragment** - 보호자용 가족 상세 정보 (통합 강화)
| 섹션 | 기능 | 구현상태 |
|-----------|------|----------|
| **가족 정보 표시** | 이름, 나이, 방호실, 요양등급, 입소일, 관계 | ✅ 완료 |
| `frame_patient_photo_detail` | 환자 사진 영역 (100x100dp, 점선 테두리, 클릭 가능) | ✅ 완료 |
| **입소 요양원 정보** | 요양원명, 주소, 연락처, 담당간호사 | ✅ 완료 |
| **📸 최근 사진** | 가족별 최근 활동 사진 목록 (기존 소식메뉴 통합) | ✅ 완료 |
| **📢 가족 전용 소식** | 개별 공지사항, 처방 변경사항, 면회 일정 등 | ✅ 완료 |
| `btn_today_meal` | 🍽️ 오늘의 급여 확인 → PatientDetailFragment(meal) | ✅ 완료 |
| `btn_today_activity` | 🎯 오늘의 활동 확인 → PatientDetailFragment(activity) | ✅ 완료 |

**통합 완료**: 기존 GuardianNewsFragment의 모든 기능을 가족별로 구분하여 통합 제공

### 10. **CaregiverPatientDetailFragment** - 직원용 환자 상세 관리 (4개 버튼)
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_meal_record` | 급여 내역 입력 → CaregiverMealRecordFragment | ✅ 완료 |
| `btn_activity_record` | 활동 프로그램 입력 → CaregiverActivityRecordFragment | ✅ 완료 |
| `btn_news_write` | 소식 입력 → NewsWriteFragment | ✅ 완료 |
| `btn_individual_notice` | 개별 공지 작성 → CaregiverIndividualNoticeFragment | ✅ 완료 |

---

## 📝 **기록 관리**

### 10. **NewsWriteFragment** - 소식 작성 (coderelief1 WriteNewsScreenPreview 기반)
| Component | 기능 | 구현상태 |
|-----------|------|----------|
| `et_news_title` | 소식 제목 입력 | ✅ 완료 |
| `et_news_content` | 소식 내용 입력 (멀티라인) | ✅ 완료 |
| `iv_photo` | 첨부 사진 표시 영역 | ✅ 완료 |
| `btn_select_photo` | 사진 선택/촬영 | ⏳ 임시구현 |
| `btn_save_news` | 임시 저장 | ⏳ 임시구현 |
| `btn_publish_news` | 게시하기 (완료 후 이전화면 복귀) | ✅ 완료 |

### 11. **CaregiverMealRecordFragment** - 급여 기록
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_save` | 급여 내역 저장 | ❌ 구현필요 |

### 11. **CaregiverActivityRecordFragment** - 활동 기록
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_photo_capture` | 사진 촬영 | ❌ 구현필요 |
| `btn_photo_gallery` | 갤러리 선택 | ❌ 구현필요 |
| `btn_save` | 활동 기록 저장 | ❌ 구현필요 |

### 12. **CaregiverIndividualNoticeFragment** - 개별 공지
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_priority_urgent` | 긴급 우선순위 설정 | ❌ 구현필요 |
| `btn_priority_important` | 중요 우선순위 설정 | ❌ 구현필요 |
| `btn_priority_normal` | 일반 우선순위 설정 | ❌ 구현필요 |
| `btn_send` | 개별 공지 전송 | ❌ 구현필요 |

---

## 🏥 **요양원 찾기**

### 13. **FindCareCenterFragment** - 요양원 검색 (RecyclerView 기반)
| Component | 기능 | 구현상태 |
|-----------|------|----------|
| `RecyclerView recycler_view_care_centers` | 동적 요양원 목록 표시 | ✅ 완료 |
| `CareCenterAdapter` | 요양원 데이터 바인딩 | ✅ 완료 |
| `CareCenter 모델 클래스` | 스키마 기반 요양원 정보 | ✅ 완료 |
| `item_care_center.xml` | 요양원 카드 아이템 레이아웃 | ✅ 완료 |
| `btn_care_center_detail` (아이템 내) | 선택된 요양원 상세정보 | ✅ 완료 |
| `지도 영역` | 팀원 작업 중 (기존 구조 유지) | 🔄 진행중 |

### 14. **CareCenterInfoFragment** - 요양원 정보
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_consultation` | 상담 예약 | ❌ 구현필요 |

### 15. **ConsultationFragment** - 상담 신청
| Button ID | 기능 | 구현상태 |
|-----------|------|----------|
| `btn_submit` | 상담 신청하기 | ❌ 구현필요 |

---

## 📊 **구현 현황 요약**

### ✅ **완료된 기능** (39개)
- 메인 네비게이션 (Guardian/Caregiver)
- 캘린더 기본 조작 (이전/다음 달)
- **동적 환자 관리 시스템** (RecyclerView 기반)
- **동적 요양원 검색 시스템** (RecyclerView 기반)
- **스키마 기반 모델 클래스** (Patient, CareCenter, Institution)
- **완전한 소식 작성 플로우** (환자 목록 → 상세 → 소식 작성)
- **NewsWriteFragment** (coderelief1 WriteNewsScreenPreview 기반)
- **보호자/직원 UI 완전 분리** (역할별 최적화된 인터페이스)
- **GuardianPatientDetailFragment 통합 강화** (사진, 공지, 급여, 활동 통합 제공)
- **FamilyMemberAdapter** (보호자용 가족 목록 어댑터)
- **요양원 정보 표시** (가족별 입소 요양원 구분)
- **보호자 메뉴 단순화** (4개 → 3개 메뉴, 중복 기능 제거)
- **가족별 최근 사진 섹션** (기존 소식메뉴 기능 통합)
- **가족 전용 공지사항 섹션** (개별화된 소식 제공)
- **GuardianChatSelectionFragment** (전용 가족별 채팅 선택 화면)
- **레이아웃 디자인 통일화** (Purple 헤더 패턴 표준화)
- **환자 사진 영역 추가** (점선 테두리 FrameLayout, 클릭 가능)
- **UI 정리 완료** (불필요한 등급 배지 제거)
- **데이터 일관성 보장** (하드코딩 제거, 공유 데이터 구조)
- **채팅 시스템 개선** (스마트바 → 레이아웃 기반 접근법)

### ⏳ **부분 구현** (3개)
- 사진 선택/촬영 기능 (UI만 완료, 기능 대기)
- 소식 임시저장 (로직 대기)
- 소식 게시 (Toast만, DB 연동 대기)

### ❌ **구현 필요한 기능** (26개)
- 비즈니스 로직 (데이터 로딩, 저장)
- API 연동 및 실제 데이터 처리
- 파일/이미지 처리 완성
- 실시간 통신
- Repository 패턴 구현

### 🗑️ **제거된 기능**
- 채팅 빠른 답장 버튼 3개 (불필요한 기능으로 판단하여 제거)
- **하드코딩된 환자 버튼들** (btn_patient1_meal, btn_patient2_meal 등 → RecyclerView로 대체)
- **하드코딩된 요양원 버튼들** (btn_detail_1, btn_detail_2 등 → RecyclerView로 대체)
- **GuardianNewsFragment** (소식 메뉴 → GuardianPatientDetailFragment에 통합)
- **보호자 메뉴 중복 기능** (4개 → 3개 메뉴로 단순화)
- **채팅 스마트바 시스템** (하단 팝업 → 전용 레이아웃 화면으로 교체)
- **요양등급 배지** (item_family_member, item_patient에서 제거)

### 🎯 **우선순위**
1. **HIGH**: ✅ 동적 환자 관리, ✅ 동적 요양원 검색, ✅ 소식 작성 플로우, 공지사항, 기록 저장
2. **MEDIUM**: 사진 업로드, 채팅, 일정 추가, Repository 패턴  
3. **LOW**: 상담 예약, 고급 검색 기능, 소식 임시저장

---

*작성일: 2025-08-20*  
*Fragment 기반 역할별 UI 분리 버전*