-- V1__init.sql에 누락된 테이블들 추가

-- 1. 데일리기록 테이블 (DailyRecord Entity용)
CREATE TABLE daily_record (
  record_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
  patient_id     BIGINT,
  caregiver_id   BIGINT,
  record_date    DATE NOT NULL,
  time_slot      ENUM('BREAKFAST','LUNCH','DINNER') NOT NULL,
  meal           ENUM('GOOD','NORMAL','BAD') NOT NULL,
  health_condition ENUM('GOOD','NORMAL','BAD'),
  medication_taken BOOLEAN NOT NULL DEFAULT FALSE,
  notes          TEXT,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (patient_id)   REFERENCES patient(patient_id),
  FOREIGN KEY (caregiver_id) REFERENCES caregiver(caregiver_id)
);

-- 2. 채팅방 테이블 (ChatRoom Entity용)
CREATE TABLE chat_room (
  chat_room_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_name      VARCHAR(255) NOT NULL,
  room_type      ENUM('GROUP','PRIVATE') NOT NULL,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 채팅메시지 테이블 (ChatMessage Entity용)
CREATE TABLE chat_message (
  message_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
  chat_room_id  BIGINT,
  sender_id     BIGINT,
  sender_type   ENUM('CAREGIVER','GUARDIAN','PATIENT') NOT NULL,
  message_text  TEXT NOT NULL,
  sent_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (chat_room_id) REFERENCES chat_room(chat_room_id)
);
