-- Guardian 테이블에 누락된 컬럼들 추가

-- 1. relationship 컬럼 추가
ALTER TABLE guardian ADD COLUMN relationship VARCHAR(50) AFTER phone;

-- 2. address 컬럼 추가
ALTER TABLE guardian ADD COLUMN address VARCHAR(255) AFTER relationship;
