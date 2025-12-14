-- V9: 비회원 상담 신청 테이블 추가 (단순화된 버전)
-- 요양원별 분류로 비회원의 상담 신청을 관리 (날짜/시간/상태 관리 기능 제거)

CREATE TABLE consultation_request (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    institution_id BIGINT NOT NULL COMMENT '요양원 ID',
    institution_name VARCHAR(255) NOT NULL COMMENT '신청할 요양원 이름',
    applicant_name VARCHAR(100) NOT NULL COMMENT '신청자 이름',
    applicant_phone VARCHAR(20) NOT NULL COMMENT '신청자 연락처',
    consultation_purpose VARCHAR(255) NOT NULL COMMENT '상담 목적',
    consultation_content TEXT NOT NULL COMMENT '상담 내용',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '신청 생성 시간',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정 시간',

    INDEX idx_institution_id (institution_id),
    INDEX idx_institution_name (institution_name),
    INDEX idx_applicant_phone (applicant_phone),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (institution_id) REFERENCES institution(institution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='비회원 상담 신청 (단순화된 버전)';

-- 테스트 데이터 삽입
INSERT INTO consultation_request (
    institution_id, institution_name, applicant_name, applicant_phone,
    consultation_purpose, consultation_content
) VALUES
(1, '요양원 A', '김철수', '010-1234-5678', '입원 상담', '어머니를 위한 요양원 입원에 대해 상담받고 싶습니다. 치매 초기 단계이고, 24시간 보호가 필요합니다.'),
(2, '요양원 B', '이영희', '010-9876-5432', '시설 견학', '요양원 시설을 직접 보고 싶습니다. 방문 가능한 시간을 알려주세요.'),
(1, '요양원 A', '박민수', '010-5555-1234', '요양 서비스 문의', '요양보호사 서비스와 비용에 대해 자세히 알고 싶습니다.');




