-- 기본 테스트 데이터 삽입

-- 1. 기관 데이터
INSERT INTO institution (name, address, phone, rating) VALUES 
('요양원 A', '서울시 강남구 테헤란로 123', '02-1234-5678', 4.5),
('요양원 B', '서울시 서초구 서초대로 456', '02-2345-6789', 4.2);

-- 2. 보호자 데이터
INSERT INTO guardian (name, phone, relationship, address, email) VALUES 
('김보호자', '010-1111-2222', '배우자', '서울시 강남구', 'guardian1@test.com'),
('이보호자', '010-3333-4444', '자녀', '서울시 서초구', 'guardian2@test.com');

-- 3. 환자 데이터
INSERT INTO patient (name, birthdate, gender, admission_date, guardian_id, institution_id) VALUES 
('김환자', '1940-01-01', 'M', '2024-01-01', 1, 1),
('이환자', '1945-02-02', 'F', '2024-01-15', 2, 2);

-- 4. 요양보호사 데이터
INSERT INTO caregiver (name, phone, staff_code, institution_id) VALUES 
('박요양사', '010-5555-6666', 'STAFF001', 1),
('최요양사', '010-7777-8888', 'STAFF002', 2);

-- 5. 활동 기록 데이터
INSERT INTO activity (patient_id, caregiver_id, type, description, activity_time) VALUES 
(1, 1, '산책', '정원에서 30분간 산책', '2025-08-27 09:00:00'),
(1, 1, '식사', '아침 식사 도움', '2025-08-27 07:30:00'),
(2, 2, '운동', '가벼운 스트레칭', '2025-08-27 10:00:00');

-- 6. 데일리 기록 데이터
INSERT INTO daily_record (patient_id, caregiver_id, record_date, time_slot, meal, health_condition, medication_taken, notes) VALUES 
(1, 1, '2025-08-27', 'BREAKFAST', 'GOOD', 'GOOD', TRUE, '식사 잘 하심'),
(1, 1, '2025-08-27', 'LUNCH', 'NORMAL', 'NORMAL', TRUE, '약 복용 완료'),
(2, 2, '2025-08-27', 'BREAKFAST', 'GOOD', 'GOOD', TRUE, '컨디션 좋음');
