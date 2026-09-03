-- Base schema for a fresh local database. Not run automatically
-- (spring.sql.init.mode=never, spring.jpa.hibernate.ddl-auto=validate) — apply once with:
--   mysql -u <user> -p sunrise_dental < src/main/resources/db/schema.sql
-- Then apply src/main/resources/db/phase6-advanced-db-objects.sql for the function,
-- stored procedure, and trigger.

CREATE TABLE IF NOT EXISTS staff_users (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    username      VARCHAR(80)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(40)  NOT NULL DEFAULT 'STAFF',
    enabled       TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY username (username)
);

CREATE TABLE IF NOT EXISTS treatments (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL,
    description VARCHAR(255),
    base_cost   DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY name (name)
);

CREATE TABLE IF NOT EXISTS patients (
    id             BIGINT NOT NULL AUTO_INCREMENT,
    full_name      VARCHAR(150) NOT NULL,
    address        VARCHAR(255) NOT NULL,
    contact_number VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS appointments (
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    appointment_number VARCHAR(30)  NOT NULL,
    patient_id         BIGINT       NOT NULL,
    treatment_id       BIGINT       NOT NULL,
    dentist_name       VARCHAR(150) NOT NULL,
    appointment_at     DATETIME     NOT NULL,
    status             VARCHAR(30)  NOT NULL DEFAULT 'BOOKED',
    PRIMARY KEY (id),
    UNIQUE KEY appointment_number (appointment_number),
    KEY fk_appointment_patient (patient_id),
    KEY fk_appointment_treatment (treatment_id),
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appointment_treatment FOREIGN KEY (treatment_id) REFERENCES treatments (id)
);

CREATE TABLE IF NOT EXISTS bills (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    appointment_id    BIGINT        NOT NULL,
    consultation_fee  DECIMAL(10,2) NOT NULL,
    treatment_cost    DECIMAL(10,2) NOT NULL,
    total_cost        DECIMAL(10,2) NOT NULL,
    issued_at         DATETIME      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY appointment_id (appointment_id),
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
);
