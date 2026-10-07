-- Reference schema (MySQL 8). The app creates/updates these tables itself via Hibernate (ddl-auto=update),
-- so running this file is OPTIONAL. Useful to show the relational design in an interview.
CREATE DATABASE IF NOT EXISTS opd_db CHARACTER SET utf8mb4;
USE opd_db;

CREATE TABLE IF NOT EXISTS patients (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100) NOT NULL,
  gender        VARCHAR(10)  NOT NULL,
  age           INT          NOT NULL,
  phone_number  VARCHAR(15)  NOT NULL UNIQUE,
  created_at    DATETIME(6)  NOT NULL,
  INDEX idx_patient_name (name)
);

CREATE TABLE IF NOT EXISTS doctors (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  name           VARCHAR(100) NOT NULL,
  specialization VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS appointments (
  id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id            BIGINT      NOT NULL,
  doctor_id             BIGINT      NOT NULL,
  appointment_date_time DATETIME(6) NOT NULL,
  status                VARCHAR(15) NOT NULL,          -- BOOKED | COMPLETED
  created_at            DATETIME(6) NOT NULL,
  CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
  CONSTRAINT fk_appt_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctors(id),
  INDEX idx_appt_datetime (appointment_date_time)
);

CREATE TABLE IF NOT EXISTS consultations (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  appointment_id BIGINT UNIQUE,                         -- 1 appointment : 0..1 consultation
  blood_pressure VARCHAR(20),
  temperature    DOUBLE,
  notes          VARCHAR(1000),
  completed_at   DATETIME(6),                           -- NULL until completed
  CONSTRAINT fk_cons_appt FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

-- Doctors are seeded by DataSeeder on first start; manual equivalent:
-- INSERT INTO doctors(name, specialization) VALUES ('Dr. Mehta','General Medicine'),('Dr. Iyer','Pediatrics'),('Dr. Khan','Orthopedics');
