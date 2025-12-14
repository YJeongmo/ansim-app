-- V18: 요양원 직원 등급 시스템 추가 및 테스트 계정 DB 연동
-- 1. 하드코딩된 테스트 계정을 실제 DB 데이터로 전환
-- 2. 요양원 직원에게 등급(role) 부여하여 권한 관리

-- ============================================================
-- 1단계: caregiver_accounts 테이블에 role 컬럼 추가
-- ============================================================
ALTER TABLE caregiver_accounts 
ADD COLUMN role ENUM('STAFF', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'STAFF' 
COMMENT '직원 등급: STAFF(일반), MANAGER(관리자-상담신청접근가능), ADMIN(최고관리자)' 
AFTER institution_code;

-- ============================================================
-- 2단계: caregiver 테이블에 role 컬럼 추가
-- ============================================================
ALTER TABLE caregiver 
ADD COLUMN role ENUM('STAFF', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'STAFF' 
COMMENT '직원 등급: STAFF(일반), MANAGER(관리자-상담신청접근가능), ADMIN(최고관리자)' 
AFTER staff_code;

-- ============================================================
-- 3단계: 기존 테스트 계정 데이터 정리 및 재구성
-- ============================================================

-- 기존 테스트 계정 삭제 (V8에서 생성된 것들)
DELETE FROM caregiver_accounts WHERE username IN ('caregiver1', 'caregiver2', 'caregiver3');
DELETE FROM guardian_accounts WHERE username IN ('guardian1', 'guardian2', 'guardian3');

-- ============================================================
-- 4단계: 실제 사용할 테스트 계정 추가
-- ============================================================

-- 4-1. 보호자 계정 추가 (하드코딩: username='a', password='1')
INSERT INTO guardian_accounts (username, password, created_at, updated_at) VALUES
('a', '1', NOW(), NOW()),  -- 하드코딩된 테스트 계정 (김환자의 보호자)
('guardian_test1', 'password123', NOW(), NOW()),
('guardian_test2', 'password123', NOW(), NOW());

-- 4-2. 요양원 직원 계정 추가 (하드코딩: username='b', password='2')
INSERT INTO caregiver_accounts (username, password, institution_code, role, created_at, updated_at) VALUES
('b', '2', '1', 'MANAGER', NOW(), NOW()),  -- 하드코딩된 테스트 계정 (관리자급)
('c', '3', '1', 'STAFF', NOW(), NOW()),    -- 하드코딩된 테스트 계정 (일반직원급) - 안요양사
('staff1', 'password123', '1', 'STAFF', NOW(), NOW()),    -- 일반 직원
('manager1', 'password123', '1', 'MANAGER', NOW(), NOW()), -- 관리자
('admin1', 'password123', '1', 'ADMIN', NOW(), NOW()),     -- 최고 관리자
('staff2', 'password123', '2', 'STAFF', NOW(), NOW());     -- 요양원 B의 일반 직원

-- ============================================================
-- 5단계: guardian 테이블과 account 연결
-- ============================================================

-- 기존 guardian 데이터 확인 (V7에서 생성된 것들)
-- guardian 1: 김보호자 (김환자와 연결)
-- guardian 2: 이보호자 (이환자와 연결)

-- guardian 1 (김보호자)를 'a' 계정과 연결
UPDATE guardian 
SET account_id = (SELECT id FROM guardian_accounts WHERE username = 'a')
WHERE guardian_id = 1;

-- guardian 2 (이보호자)를 'guardian_test1' 계정과 연결
UPDATE guardian 
SET account_id = (SELECT id FROM guardian_accounts WHERE username = 'guardian_test1')
WHERE guardian_id = 2;

-- ============================================================
-- 6단계: caregiver 테이블과 account 연결
-- ============================================================

-- 기존 caregiver 데이터 확인 (V7에서 생성된 것들)
-- caregiver 1: 박요양사 (요양원 A)
-- caregiver 2: 최요양사 (요양원 B)

-- caregiver 1 (박요양사)를 'b' 계정과 연결, MANAGER 등급 부여
UPDATE caregiver 
SET account_id = (SELECT id FROM caregiver_accounts WHERE username = 'b'),
    role = 'MANAGER'
WHERE caregiver_id = 1;

-- caregiver 2 (최요양사)를 'staff2' 계정과 연결, STAFF 등급 유지
UPDATE caregiver 
SET account_id = (SELECT id FROM caregiver_accounts WHERE username = 'staff2'),
    role = 'STAFF'
WHERE caregiver_id = 2;

-- ============================================================
-- 7단계: 추가 테스트용 직원 데이터 생성
-- ============================================================

-- 요양원 A에 추가 직원 생성
INSERT INTO caregiver (name, phone, staff_code, institution_id, role, account_id, created_at) VALUES
('안요양사', '010-4444-4444', 'STAFF002-AN', 1, 'STAFF', 
 (SELECT id FROM caregiver_accounts WHERE username = 'c'), NOW()),  -- 하드코딩 계정 'c'와 연결
('김일반', '010-1111-1111', 'STAFF001-NEW', 1, 'STAFF', 
 (SELECT id FROM caregiver_accounts WHERE username = 'staff1'), NOW()),
('이관리', '010-2222-2222', 'MANAGER001', 1, 'MANAGER', 
 (SELECT id FROM caregiver_accounts WHERE username = 'manager1'), NOW()),
('박최고', '010-3333-3333', 'ADMIN001', 1, 'ADMIN', 
 (SELECT id FROM caregiver_accounts WHERE username = 'admin1'), NOW());

-- ============================================================
-- 8단계: 환자 데이터 확인 및 검증
-- ============================================================
-- 기존 환자 데이터 (V7에서 생성):
-- patient 1: 김환자 (guardian_id=1, institution_id=1)
-- patient 2: 이환자 (guardian_id=2, institution_id=2)
-- 
-- 이미 존재하므로 추가 작업 불필요

-- ============================================================
-- 9단계: 인덱스 추가 (성능 최적화)
-- ============================================================
CREATE INDEX idx_caregiver_role ON caregiver(role);
CREATE INDEX idx_caregiver_account_role ON caregiver_accounts(role);

-- ============================================================
-- 완료 상태 요약
-- ============================================================
-- ✅ caregiver_accounts에 role 컬럼 추가됨
-- ✅ caregiver에 role 컬럼 추가됨
-- ✅ 하드코딩 테스트 계정 ('a'/'1', 'b'/'2', 'c'/'3') DB에 추가됨
-- ✅ guardian과 guardian_accounts 연결됨
-- ✅ caregiver와 caregiver_accounts 연결됨
-- ✅ 다양한 등급의 테스트 직원 생성됨
-- ✅ 안요양사 (c/3, STAFF) 계정 추가됨 - 김환자와 같은 요양원 A
-- ✅ 기존 환자 데이터 유지됨 (김환자, 이환자)


