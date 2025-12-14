/* ---------- 기본 테이블 ---------- */

/* 1. 기관(요양원) */
CREATE TABLE institution (
  institution_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(255) NOT NULL,
  address        VARCHAR(255),
  phone          VARCHAR(50),
  rating         DECIMAL(2,1) DEFAULT 0.0,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* 2. 보호자 */
CREATE TABLE guardian (
  guardian_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  phone       VARCHAR(50),
  email       VARCHAR(100),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* 3. 요양보호사 */
CREATE TABLE caregiver (
  caregiver_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(100) NOT NULL,
  phone          VARCHAR(50),
  staff_code     VARCHAR(50) UNIQUE,
  institution_id BIGINT,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (institution_id) REFERENCES institution(institution_id)
);

/* 4. 환자 */
CREATE TABLE patient (
  patient_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
  name           VARCHAR(100) NOT NULL,
  birthdate      DATE,
  gender         ENUM('M','F'),
  admission_date DATE DEFAULT (CURRENT_DATE),
  guardian_id    BIGINT UNIQUE,
  institution_id BIGINT,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (guardian_id)    REFERENCES guardian(guardian_id),
  FOREIGN KEY (institution_id) REFERENCES institution(institution_id)
);

/* ---------- 활동 · 공지 · 일정 ---------- */

/* 5. 활동 기록 */
CREATE TABLE activity (
  activity_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  patient_id    BIGINT,
  caregiver_id  BIGINT,
  type          VARCHAR(100),          -- 예: Meal, Medication, Program
  description   TEXT,
  photo_url     VARCHAR(255),
  activity_time DATETIME NOT NULL,
  FOREIGN KEY (patient_id)   REFERENCES patient(patient_id),
  FOREIGN KEY (caregiver_id) REFERENCES caregiver(caregiver_id)
);

/* 6. 공지 */
CREATE TABLE notice (
  notice_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  institution_id  BIGINT,
  title           VARCHAR(255) NOT NULL,
  content         TEXT,
  is_personal     BOOLEAN DEFAULT FALSE,
  patient_id      BIGINT,
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (institution_id) REFERENCES institution(institution_id),
  FOREIGN KEY (patient_id)     REFERENCES patient(patient_id)
);

/* 7. 일정/면담 예약 */
CREATE TABLE appointment (
  appointment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  patient_id     BIGINT,
  guardian_id    BIGINT,
  scheduled_at   DATETIME NOT NULL,
  purpose        VARCHAR(255),
  status         ENUM('REQUEST','APPROVED','REJECTED') DEFAULT 'REQUEST',
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (patient_id)  REFERENCES patient(patient_id),
  FOREIGN KEY (guardian_id) REFERENCES guardian(guardian_id)
);
