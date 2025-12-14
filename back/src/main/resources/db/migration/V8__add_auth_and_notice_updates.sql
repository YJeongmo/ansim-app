-- V8: 인증 시스템 및 공지사항 업데이트
-- 기존 ansim_yoyang DB에 새로운 기능 추가

-- 1. 인증 관련 테이블 추가

-- 보호자 계정 테이블
CREATE TABLE IF NOT EXISTS guardian_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '아이디',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='보호자 계정 정보';

-- 요양원 직원 계정 테이블
CREATE TABLE IF NOT EXISTS caregiver_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '아이디',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화)',
    institution_code VARCHAR(20) NOT NULL COMMENT '기관코드',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_institution_code (institution_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='요양원 직원 계정 정보';

-- 2. 기존 테이블에 인증 연결 컬럼 추가

-- Guardian 테이블에 account_id 추가 (name 컬럼 뒤에 추가)
ALTER TABLE guardian ADD COLUMN account_id BIGINT NULL AFTER name;

-- Guardian 테이블에 외래키 제약조건 추가
ALTER TABLE guardian ADD CONSTRAINT fk_guardian_account 
    FOREIGN KEY (account_id) REFERENCES guardian_accounts(id) ON DELETE SET NULL;

-- Caregiver 테이블에 account_id 추가 (name 컬럼 뒤에 추가)
ALTER TABLE caregiver ADD COLUMN account_id BIGINT NULL AFTER name;

-- Caregiver 테이블에 외래키 제약조건 추가
ALTER TABLE caregiver ADD CONSTRAINT fk_caregiver_account 
    FOREIGN KEY (account_id) REFERENCES caregiver_accounts(id) ON DELETE SET NULL;

-- 3. Notice 테이블 구조 업데이트

-- 기존 notice 테이블에 새로운 컬럼들 추가 (caregiver_id 뒤에 추가)
ALTER TABLE notice ADD COLUMN author_id BIGINT NULL AFTER caregiver_id;
ALTER TABLE notice ADD COLUMN author_type ENUM('guardian', 'caregiver') NULL AFTER author_id;

-- 4. 기존 데이터와의 호환성을 위한 인덱스 추가
CREATE INDEX idx_notice_author ON notice(author_id, author_type);
CREATE INDEX idx_notice_created_at ON notice(created_at);

-- 5. 테스트 계정 데이터 삽입 (BCrypt 암호화된 'password')

-- 보호자 계정
INSERT INTO guardian_accounts (username, password) VALUES
('guardian1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'),
('guardian2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'),
('guardian3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa');

-- 요양원 직원 계정 (실제 institution 테이블의 institution_id 사용)
INSERT INTO caregiver_accounts (username, password, institution_code) VALUES
('caregiver1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '1'),
('caregiver2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '1'),
('caregiver3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '2');