-- 환자 테이블에 성별 컬럼 추가 (이미 존재하는 경우 무시)
-- MySQL에서는 IF NOT EXISTS를 지원하지 않으므로, 컬럼이 이미 존재하는지 확인 후 추가

-- 컬럼이 존재하지 않는 경우에만 추가
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'patient' 
     AND COLUMN_NAME = 'gender') = 0,
    'ALTER TABLE patient ADD COLUMN gender ENUM(''M'',''F'') AFTER birthdate',
    'SELECT ''Column gender already exists'' as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 기존 데이터에 성별 정보 업데이트 (임시 데이터)
UPDATE patient SET gender = 'M' WHERE patient_id = 1 AND gender IS NULL;
UPDATE patient SET gender = 'F' WHERE patient_id = 2 AND gender IS NULL;
