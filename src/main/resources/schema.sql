-- Reference schema for MySQL. Run this script when creating a local database.
CREATE DATABASE IF NOT EXISTS sunrise_dental;
USE sunrise_dental;

CREATE TABLE IF NOT EXISTS staff_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(40) NOT NULL DEFAULT 'STAFF',
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS treatments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    base_cost DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,
    dentist_name VARCHAR(150) NOT NULL,
    appointment_at DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'BOOKED',
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_appointment_treatment FOREIGN KEY (treatment_id) REFERENCES treatments(id)
);

CREATE TABLE IF NOT EXISTS bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL UNIQUE,
    consultation_fee DECIMAL(10, 2) NOT NULL,
    treatment_cost DECIMAL(10, 2) NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    issued_at DATETIME NOT NULL,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);
