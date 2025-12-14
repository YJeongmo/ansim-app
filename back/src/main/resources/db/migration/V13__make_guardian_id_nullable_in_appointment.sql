-- Make guardian_id nullable in appointment table for caregiver-created appointments
ALTER TABLE appointment MODIFY COLUMN guardian_id BIGINT NULL;
