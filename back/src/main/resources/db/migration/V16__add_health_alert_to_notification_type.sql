-- notification_type에 HEALTH_ALERT 추가
ALTER TABLE notifications MODIFY COLUMN notification_type 
ENUM('CHAT', 'MEAL', 'ACTIVITY', 'NOTICE', 'APPOINTMENT', 'CONSULTATION', 'HEALTH_ALERT') 
NOT NULL COMMENT '알림 타입';
