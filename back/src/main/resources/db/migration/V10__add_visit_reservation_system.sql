-- V10: 면회 예약 시스템 추가
-- 회원용 면회, 외출, 외박, 상담 예약 관리

-- 1. 요양원 기본 설정 테이블
CREATE TABLE IF NOT EXISTS institution_settings (
    setting_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    institution_id BIGINT NOT NULL,
    
    -- 기본 면회 시간
    visit_start_time TIME DEFAULT '09:00:00' COMMENT '면회 시작 시간',
    visit_end_time TIME DEFAULT '20:00:00' COMMENT '면회 종료 시간',
    
    -- 인원 제한
    max_concurrent_visits INT DEFAULT 5 COMMENT '동시 면회 최대 건수',
    max_visitors_per_reservation INT DEFAULT 3 COMMENT '예약당 최대 방문자 수',
    
    -- 예약 정책
    advance_booking_days INT DEFAULT 14 COMMENT '사전 예약 가능 일수',
    cancellation_deadline_hours INT DEFAULT 24 COMMENT '취소 마감 시간 (시간 단위)',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (institution_id) REFERENCES institution(institution_id),
    UNIQUE KEY uk_institution_settings (institution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='요양원별 면회 예약 설정';

-- 2. 특정 날짜 제한 테이블
CREATE TABLE IF NOT EXISTS institution_date_restrictions (
    restriction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    institution_id BIGINT NOT NULL,
    
    restriction_date DATE NOT NULL COMMENT '제한 적용 날짜',
    restriction_type ENUM('LIMITED', 'EXTENDED') NOT NULL COMMENT '제한 유형',
    
    -- LIMITED: 제한 운영 (시간/인원 축소)
    -- EXTENDED: 연장 운영 (특별한 날)
    
    custom_start_time TIME NULL COMMENT '사용자 정의 시작 시간',
    custom_end_time TIME NULL COMMENT '사용자 정의 종료 시간',
    custom_max_visits INT NULL COMMENT '사용자 정의 최대 면회 건수',
    custom_max_visitors INT NULL COMMENT '사용자 정의 최대 방문자 수',
    
    reason VARCHAR(255) COMMENT '제한 사유 (예: 정기 소독, 특별 행사)',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (institution_id) REFERENCES institution(institution_id),
    UNIQUE KEY uk_institution_date (institution_id, restriction_date),
    INDEX idx_restriction_date (restriction_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='요양원별 특정 날짜 제한 설정';

-- 3. 기존 appointment 테이블 확장 - 누락된 컬럼들 추가

-- Hibernate 엔터티에서 필요한 컬럼들 추가
ALTER TABLE appointment ADD COLUMN appointment_type ENUM('VISIT', 'OUTING', 'OVERNIGHT', 'CONSULTATION') DEFAULT 'VISIT' COMMENT '예약 유형' AFTER purpose;

ALTER TABLE appointment ADD COLUMN start_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '시작 시간' AFTER appointment_type;

ALTER TABLE appointment ADD COLUMN end_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '종료 시간' AFTER start_time;

ALTER TABLE appointment ADD COLUMN reason VARCHAR(500) COMMENT '예약 사유' AFTER end_time;

ALTER TABLE appointment ADD COLUMN guardian_notes TEXT COMMENT '보호자 메모' AFTER reason;

ALTER TABLE appointment ADD COLUMN staff_notes TEXT COMMENT '직원 메모' AFTER guardian_notes;

ALTER TABLE appointment ADD COLUMN visitor_relationship VARCHAR(50) COMMENT '방문자와 어르신과의 관계' AFTER staff_notes;

ALTER TABLE appointment ADD COLUMN visitor_count INT DEFAULT 1 COMMENT '방문자 수' AFTER visitor_relationship;

ALTER TABLE appointment ADD COLUMN approved_by BIGINT COMMENT '승인자 ID' AFTER visitor_count;

ALTER TABLE appointment ADD COLUMN processed_at DATETIME COMMENT '처리 시각' AFTER approved_by;

ALTER TABLE appointment ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각' AFTER processed_at;

-- 인덱스 추가
CREATE INDEX idx_appointment_start_time ON appointment(start_time);
CREATE INDEX idx_appointment_type ON appointment(appointment_type);

-- 외래키 제약조건 추가
ALTER TABLE appointment 
ADD CONSTRAINT fk_appointment_approved_by 
FOREIGN KEY (approved_by) REFERENCES caregiver(caregiver_id) ON DELETE SET NULL;

-- 상태 ENUM 업데이트 (기존 값 유지하며 확장)
ALTER TABLE appointment 
MODIFY COLUMN status ENUM('REQUEST', 'APPROVED', 'REJECTED', 'PENDING', 'CANCELLED') DEFAULT 'PENDING' COMMENT '예약 상태';

-- 인덱스 추가 (이미 있을 수 있으므로 주석 처리)
-- ALTER TABLE appointment 
-- ADD INDEX idx_appointment_datetime (start_time, end_time),
-- ADD INDEX idx_appointment_type_status (appointment_type, status),
-- ADD INDEX idx_appointment_processed (processed_at);

-- 4. 동반자 정보 테이블
CREATE TABLE IF NOT EXISTS reservation_companions (
    companion_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL,
    
    companion_name VARCHAR(100) NOT NULL COMMENT '동반자 이름',
    companion_relationship VARCHAR(50) COMMENT '환자와의 관계 (딸, 아들, 며느리 등)',
    companion_age INT COMMENT '동반자 나이',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE CASCADE,
    INDEX idx_companion_appointment (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='예약별 동반자 정보';

-- 5. 예약 히스토리 테이블 (상태 변경 추적)
CREATE TABLE IF NOT EXISTS appointment_history (
    history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL,
    
    status_from VARCHAR(50) NOT NULL COMMENT '변경 전 상태',
    status_to VARCHAR(50) NOT NULL COMMENT '변경 후 상태',
    
    changed_by BIGINT COMMENT '변경자 ID',
    changed_by_type ENUM('GUARDIAN', 'CAREGIVER') NOT NULL COMMENT '변경자 유형',
    change_reason TEXT COMMENT '변경 사유',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE CASCADE,
    INDEX idx_appointment_history (appointment_id, created_at),
    INDEX idx_history_changed_by (changed_by, changed_by_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='예약 상태 변경 히스토리';

-- 6. 기본 데이터 삽입

-- 기존 요양원들에 대한 기본 설정 추가 (중복 방지)
INSERT IGNORE INTO institution_settings (institution_id, visit_start_time, visit_end_time, max_concurrent_visits, max_visitors_per_reservation, advance_booking_days, cancellation_deadline_hours)
SELECT institution_id, '09:00:00', '20:00:00', 5, 3, 14, 24
FROM institution;

-- 테스트용 예약 데이터는 실제 데이터가 있는 경우에만 추가
-- (이 부분은 수동으로 실행하거나 실제 데이터 확인 후 추가)
-- 기존 appointment 데이터가 있다면 기본값으로 업데이트는 위에서 이미 처리됨