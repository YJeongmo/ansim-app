-- 일관성을 위해 누락된 created_at 컬럼들 추가

-- Activity 테이블에 created_at 컬럼 추가
ALTER TABLE activity ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER activity_time;
