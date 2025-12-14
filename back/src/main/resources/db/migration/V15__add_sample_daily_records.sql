-- 샘플 데일리기록 데이터 추가 (건강상태 분석 테스트용)

-- 환자 1번 (김환자)의 5일간 데일리기록
INSERT INTO daily_record (patient_id, caregiver_id, record_date, time_slot, meal, health_condition, medication_taken, notes) VALUES
(1, 1, '2025-09-10', 'BREAKFAST', 'GOOD', 'GOOD', 1, '아침 식사 잘 드심, 컨디션 양호'),
(1, 1, '2025-09-10', 'LUNCH', 'NORMAL', 'NORMAL', 1, '점심 식사 보통, 약 복용 완료'),
(1, 1, '2025-09-10', 'DINNER', 'GOOD', 'GOOD', 1, '저녁 식사 잘 드심, 기분 좋아 보임'),

(1, 1, '2025-09-11', 'BREAKFAST', 'NORMAL', 'NORMAL', 1, '아침 식사 보통, 약 복용'),
(1, 1, '2025-09-11', 'LUNCH', 'GOOD', 'GOOD', 1, '점심 식사 잘 드심, 활발함'),
(1, 1, '2025-09-11', 'DINNER', 'NORMAL', 'NORMAL', 1, '저녁 식사 보통, 피로감 있음'),

(1, 1, '2025-09-12', 'BREAKFAST', 'BAD', 'BAD', 0, '아침 식사 거부, 약 복용 거부, 컨디션 안 좋음'),
(1, 1, '2025-09-12', 'LUNCH', 'NORMAL', 'NORMAL', 1, '점심은 조금 드심, 약 복용 완료'),
(1, 1, '2025-09-12', 'DINNER', 'NORMAL', 'NORMAL', 1, '저녁 식사 보통, 기분 회복됨'),

(1, 1, '2025-09-13', 'BREAKFAST', 'GOOD', 'GOOD', 1, '아침 식사 잘 드심, 컨디션 좋음'),
(1, 1, '2025-09-13', 'LUNCH', 'GOOD', 'GOOD', 1, '점심 식사 잘 드심, 활발함'),
(1, 1, '2025-09-13', 'DINNER', 'GOOD', 'GOOD', 1, '저녁 식사 잘 드심, 기분 좋음'),

(1, 1, '2025-09-14', 'BREAKFAST', 'NORMAL', 'NORMAL', 1, '아침 식사 보통, 약 복용'),
(1, 1, '2025-09-14', 'LUNCH', 'GOOD', 'GOOD', 1, '점심 식사 잘 드심, 컨디션 양호'),
(1, 1, '2025-09-14', 'DINNER', 'NORMAL', 'NORMAL', 1, '저녁 식사 보통, 약간 피로함');

-- 환자 2번 (이환자)의 5일간 데일리기록
INSERT INTO daily_record (patient_id, caregiver_id, record_date, time_slot, meal, health_condition, medication_taken, notes) VALUES
(2, 2, '2025-09-10', 'BREAKFAST', 'GOOD', 'GOOD', 1, '아침 식사 잘 드심, 컨디션 양호'),
(2, 2, '2025-09-10', 'LUNCH', 'GOOD', 'GOOD', 1, '점심 식사 잘 드심, 활발함'),
(2, 2, '2025-09-10', 'DINNER', 'GOOD', 'GOOD', 1, '저녁 식사 잘 드심, 기분 좋음'),

(2, 2, '2025-09-11', 'BREAKFAST', 'GOOD', 'GOOD', 1, '아침 식사 잘 드심, 약 복용'),
(2, 2, '2025-09-11', 'LUNCH', 'NORMAL', 'NORMAL', 1, '점심 식사 보통, 약간 피로함'),
(2, 2, '2025-09-11', 'DINNER', 'GOOD', 'GOOD', 1, '저녁 식사 잘 드심, 컨디션 회복'),

(2, 2, '2025-09-12', 'BREAKFAST', 'NORMAL', 'NORMAL', 1, '아침 식사 보통, 약 복용 완료'),
(2, 2, '2025-09-12', 'LUNCH', 'GOOD', 'GOOD', 1, '점심 식사 잘 드심, 활발함'),
(2, 2, '2025-09-12', 'DINNER', 'NORMAL', 'NORMAL', 1, '저녁 식사 보통, 기분 좋음'),

(2, 2, '2025-09-13', 'BREAKFAST', 'GOOD', 'GOOD', 1, '아침 식사 잘 드심, 컨디션 좋음'),
(2, 2, '2025-09-13', 'LUNCH', 'GOOD', 'GOOD', 1, '점심 식사 잘 드심, 활발함'),
(2, 2, '2025-09-13', 'DINNER', 'GOOD', 'GOOD', 1, '저녁 식사 잘 드심, 기분 좋음'),

(2, 2, '2025-09-14', 'BREAKFAST', 'GOOD', 'GOOD', 1, '아침 식사 잘 드심, 약 복용'),
(2, 2, '2025-09-14', 'LUNCH', 'GOOD', 'GOOD', 1, '점심 식사 잘 드심, 컨디션 양호'),
(2, 2, '2025-09-14', 'DINNER', 'GOOD', 'GOOD', 1, '저녁 식사 잘 드심, 기분 좋음');
