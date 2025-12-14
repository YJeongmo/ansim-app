-- Notice 테이블 수정

-- 1. caregiver_id 컬럼 추가
ALTER TABLE notice ADD COLUMN caregiver_id BIGINT AFTER institution_id;

-- 2. caregiver_id에 대한 외래키 제약조건 추가
ALTER TABLE notice ADD CONSTRAINT fk_notice_caregiver FOREIGN KEY (caregiver_id) REFERENCES caregiver(caregiver_id);

-- 3. is_personal 컬럼 타입을 BOOLEAN으로 변경 (MySQL에서는 TINYINT(1)과 동일하지만 명시적으로 표시)
-- MySQL에서는 BOOLEAN이 TINYINT(1)의 별칭이므로 실제로는 변경되지 않지만 명확성을 위해 표시

