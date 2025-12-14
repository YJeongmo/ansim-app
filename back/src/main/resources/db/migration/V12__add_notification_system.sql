-- V12: 알림 시스템 테이블 추가
-- Android 전용 FCM 기반 푸시 알림 시스템

-- 1. 사용자 디바이스 토큰 관리 테이블 (Android 전용)
CREATE TABLE IF NOT EXISTS user_devices (
    device_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    user_type ENUM('GUARDIAN', 'CAREGIVER') NOT NULL COMMENT '사용자 타입',
    fcm_token TEXT NOT NULL COMMENT 'Firebase Cloud Messaging 토큰',
    device_info VARCHAR(255) COMMENT '기기 정보 (모델명, 앱 버전 등)',
    is_active BOOLEAN DEFAULT TRUE COMMENT '토큰 활성 상태',
    last_used_at TIMESTAMP NULL COMMENT '마지막 사용 시간',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록 시간',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    
    INDEX idx_user (user_id, user_type),
    INDEX idx_active (is_active),
    INDEX idx_last_used (last_used_at),
    UNIQUE KEY uk_fcm_token (fcm_token(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 디바이스 토큰 관리 (Android FCM)';

-- 2. 통합 알림 테이블
CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '수신자 ID',
    user_type ENUM('GUARDIAN', 'CAREGIVER') NOT NULL COMMENT '수신자 타입',
    title VARCHAR(255) NOT NULL COMMENT '알림 제목',
    message TEXT NOT NULL COMMENT '알림 내용',
    notification_type ENUM('CHAT', 'MEAL', 'ACTIVITY', 'NOTICE', 'APPOINTMENT', 'CONSULTATION') NOT NULL COMMENT '알림 타입',
    related_id BIGINT COMMENT '관련 엔티티 ID (채팅방ID, 활동ID, 공지ID 등)',
    related_data JSON COMMENT '추가 데이터 (JSON 형태)',
    is_sent BOOLEAN DEFAULT FALSE COMMENT 'FCM 발송 완료 여부',
    is_read BOOLEAN DEFAULT FALSE COMMENT '읽음 상태',
    sent_at TIMESTAMP NULL COMMENT 'FCM 발송 시간',
    read_at TIMESTAMP NULL COMMENT '읽음 시간',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '알림 생성 시간',
    
    INDEX idx_user (user_id, user_type),
    INDEX idx_type (notification_type),
    INDEX idx_sent (is_sent),
    INDEX idx_read (is_read),
    INDEX idx_created (created_at),
    INDEX idx_related (related_id, notification_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='통합 알림 관리';

-- 3. 알림 설정 테이블 (사용자별 알림 On/Off)
CREATE TABLE IF NOT EXISTS notification_settings (
    setting_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    user_type ENUM('GUARDIAN', 'CAREGIVER') NOT NULL COMMENT '사용자 타입',
    chat_notifications BOOLEAN DEFAULT TRUE COMMENT '채팅 알림',
    meal_notifications BOOLEAN DEFAULT TRUE COMMENT '급여 알림',
    activity_notifications BOOLEAN DEFAULT TRUE COMMENT '활동 알림',
    notice_notifications BOOLEAN DEFAULT TRUE COMMENT '공지사항 알림',
    appointment_notifications BOOLEAN DEFAULT TRUE COMMENT '예약 관련 알림',
    consultation_notifications BOOLEAN DEFAULT TRUE COMMENT '상담 관련 알림',
    quiet_hours_enabled BOOLEAN DEFAULT FALSE COMMENT '조용한 시간 활성화',
    quiet_hours_start TIME DEFAULT '22:00:00' COMMENT '조용한 시간 시작',
    quiet_hours_end TIME DEFAULT '08:00:00' COMMENT '조용한 시간 종료',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '설정 생성 시간',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '설정 수정 시간',
    
    UNIQUE KEY uk_user_settings (user_id, user_type),
    INDEX idx_user (user_id, user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자별 알림 설정';

-- 4. 테스트 데이터 삽입 (기존 사용자 기반)
-- 보호자 기본 알림 설정
INSERT IGNORE INTO notification_settings (user_id, user_type) 
SELECT guardian_id, 'GUARDIAN' FROM guardian;

-- 요양보호사 기본 알림 설정  
INSERT IGNORE INTO notification_settings (user_id, user_type)
SELECT caregiver_id, 'CAREGIVER' FROM caregiver;

-- 테스트용 FCM 토큰 (실제 개발시 앱에서 등록)
INSERT IGNORE INTO user_devices (user_id, user_type, fcm_token, device_info) VALUES
(1, 'GUARDIAN', 'test_fcm_token_guardian_1', 'Android Test Device - Guardian'),
(1, 'CAREGIVER', 'test_fcm_token_caregiver_1', 'Android Test Device - Caregiver');

-- 테스트용 알림 데이터
INSERT IGNORE INTO notifications (user_id, user_type, title, message, notification_type, related_id) VALUES
(1, 'GUARDIAN', '새로운 채팅 메시지', '요양보호사님이 메시지를 보냈습니다.', 'CHAT', 1),
(1, 'GUARDIAN', '오늘의 급식', '어머니께서 점심식사를 맛있게 드셨습니다.', 'MEAL', 1),
(1, 'CAREGIVER', '새로운 면회 신청', '김가족님이 면회를 신청하셨습니다.', 'APPOINTMENT', 1);