-- Notice 테이블에 누락된 컬럼들 추가

-- 1. priority 컬럼 추가
ALTER TABLE notice ADD COLUMN priority VARCHAR(20) AFTER is_personal;

-- 2. photo_url 컬럼 추가
ALTER TABLE notice ADD COLUMN photo_url VARCHAR(255) AFTER priority;

-- 3. updated_at 컬럼 추가
ALTER TABLE notice ADD COLUMN updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

