-- V11: 채팅 시스템 테이블 생성
-- 환자별 채팅방과 메시지 관리

-- 1. 채팅방 테이블 (환자별 1개씩)
CREATE TABLE chat_room (
    chat_room_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL COMMENT '환자 ID',
    room_name VARCHAR(255) NOT NULL COMMENT '채팅방 이름 (환자명 + 요양원명)',
    room_type ENUM('PATIENT_CARE') NOT NULL DEFAULT 'PATIENT_CARE' COMMENT '채팅방 타입',
    guardian_id BIGINT COMMENT '보호자 ID',
    caregiver_id BIGINT COMMENT '요양보호사 ID',
    institution_id BIGINT COMMENT '요양원 ID',
    is_active BOOLEAN DEFAULT TRUE COMMENT '채팅방 활성 상태',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_patient_room (patient_id),
    INDEX idx_guardian (guardian_id),
    INDEX idx_caregiver (caregiver_id),
    INDEX idx_institution (institution_id),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='환자별 채팅방';

-- 2. 채팅 메시지 테이블
CREATE TABLE chat_message (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL COMMENT '발신자 ID',
    sender_type ENUM('GUARDIAN', 'CAREGIVER') NOT NULL COMMENT '발신자 타입',
    message_text TEXT NOT NULL COMMENT '메시지 내용',
    message_type ENUM('TEXT', 'IMAGE', 'FILE') DEFAULT 'TEXT' COMMENT '메시지 타입',
    is_read BOOLEAN DEFAULT FALSE COMMENT '읽음 상태',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '전송 시간',
    
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(chat_room_id) ON DELETE CASCADE,
    INDEX idx_chat_room (chat_room_id),
    INDEX idx_sender (sender_id, sender_type),
    INDEX idx_sent_at (sent_at),
    INDEX idx_read_status (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='채팅 메시지';

-- 3. 채팅방 참여자 테이블 (확장성을 위해)
CREATE TABLE chat_room_participant (
    participant_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    user_type ENUM('GUARDIAN', 'CAREGIVER') NOT NULL COMMENT '사용자 타입',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '참여 시간',
    last_read_at TIMESTAMP NULL COMMENT '마지막 읽은 시간',
    
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(chat_room_id) ON DELETE CASCADE,
    UNIQUE KEY uk_room_user (chat_room_id, user_id, user_type),
    INDEX idx_user (user_id, user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='채팅방 참여자';

-- 4. 테스트 데이터 삽입 (기존 환자 데이터 기반)
-- 환자 ID 1에 대한 채팅방 생성
INSERT INTO chat_room (patient_id, room_name, guardian_id, caregiver_id, institution_id) 
VALUES (1, '김환자님 채팅방', 1, 1, 1);

-- 채팅방 참여자 추가
INSERT INTO chat_room_participant (chat_room_id, user_id, user_type) VALUES
(1, 1, 'GUARDIAN'),
(1, 1, 'CAREGIVER');

-- 테스트 메시지 삽입
INSERT INTO chat_message (chat_room_id, sender_id, sender_type, message_text) VALUES
(1, 1, 'GUARDIAN', '안녕하세요. 어머니 상태는 어떤가요?'),
(1, 1, 'CAREGIVER', '안녕하세요! 오늘은 기분이 좋으시고 식사도 잘 드셨어요.'),
(1, 1, 'GUARDIAN', '감사합니다. 내일 면회 가능한가요?'),
(1, 1, 'CAREGIVER', '네, 가능합니다. 오후 2시 이후에 오시면 됩니다.');


