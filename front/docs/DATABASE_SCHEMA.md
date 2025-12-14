# 🗄️ 데이터베이스 스키마 설계

## 📋 현재 테이블 구조 (조원 협의 완료)

### 기본 엔티티
- **institution**: 요양원 정보
- **guardian**: 보호자 정보  
- **caregiver**: 요양보호사 정보
- **patient**: 환자 정보 (guardian 1:1 매칭)

### 기능별 테이블
- **activity**: 환자 활동 기록 (급여, 약물, 프로그램)
- **notice**: 공지사항 (전체/개별 공지 구분)
- **appointment**: 면담/예약 관리

## 🎯 Button 기능과 테이블 매핑

### GuardianNewsFragment
| 버튼 | 사용 테이블 | 쿼리 유형 |
|------|-------------|-----------|
| `card_notice_list` | notice | SELECT (기관별 공지) |
| `card_patient_photos` | activity | SELECT (photo_url 필터링) |
| `card_health_updates` | activity | SELECT (type='Medication') |
| `card_activities` | activity | SELECT (type='Program') |
| `card_meal_menu` | activity | SELECT (type='Meal') |

### CaregiverActivityRecordFragment  
| 버튼 | 사용 테이블 | 쿼리 유형 |
|------|-------------|-----------|
| `btn_photo_capture` | activity | INSERT (photo_url) |
| `btn_save` (activity) | activity | INSERT (type='Program') |
| `btn_save` (meal) | activity | INSERT (type='Meal') |

### WriteNoticeFragment
| 버튼 | 사용 테이블 | 쿼리 유형 |
|------|-------------|-----------|
| `btn_publish` | notice | INSERT |
| `btn_send` (개별공지) | notice | INSERT (is_personal=true) |

### ScheduleFragment
| 버튼 | 사용 테이블 | 쿼리 유형 |
|------|-------------|-----------|
| `btn_add_schedule` | appointment | INSERT |
| 모든 일정 조회 | appointment | SELECT (날짜별) |

## ⚠️ 추가 필요 테이블

### 1. 채팅 시스템
```sql
CREATE TABLE chat_room (
  room_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
  patient_id  BIGINT,
  guardian_id BIGINT,
  caregiver_id BIGINT,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (patient_id)  REFERENCES patient(patient_id),
  FOREIGN KEY (guardian_id) REFERENCES guardian(guardian_id),
  FOREIGN KEY (caregiver_id) REFERENCES caregiver(caregiver_id)
);

CREATE TABLE chat_message (
  message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id    BIGINT,
  sender_type ENUM('GUARDIAN','CAREGIVER'),
  sender_id   BIGINT,
  content     TEXT,
  sent_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES chat_room(room_id)
);
```

### 2. 임시저장 지원 (선택적)
```sql
CREATE TABLE notice_draft (
  draft_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  caregiver_id   BIGINT,
  title          VARCHAR(255),
  content        TEXT,
  is_personal    BOOLEAN DEFAULT FALSE,
  patient_id     BIGINT,
  saved_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (caregiver_id) REFERENCES caregiver(caregiver_id),
  FOREIGN KEY (patient_id)   REFERENCES patient(patient_id)
);
```

## 🔍 인덱스 최적화 권장사항

### 자주 조회되는 컬럼들
```sql
-- 기관별 데이터 조회 최적화
CREATE INDEX idx_patient_institution ON patient(institution_id);
CREATE INDEX idx_caregiver_institution ON caregiver(institution_id);
CREATE INDEX idx_notice_institution ON notice(institution_id);

-- 환자별 활동 조회 최적화  
CREATE INDEX idx_activity_patient_time ON activity(patient_id, activity_time DESC);

-- 개별 공지 조회 최적화
CREATE INDEX idx_notice_personal ON notice(is_personal, patient_id);

-- 예약 상태별 조회 최적화
CREATE INDEX idx_appointment_status ON appointment(status, scheduled_at);
```

## 📊 데이터 관계 다이어그램

```
institution (1) ←→ (N) patient (1) ←→ (1) guardian
     ↑                    ↓
     └── (N) caregiver    └── (N) activity
                          └── (N) appointment
                          └── (N) notice (개별)
```

---
*최종 업데이트: 2025-08-19*  
*팀원 협의 완료된 스키마 기준*