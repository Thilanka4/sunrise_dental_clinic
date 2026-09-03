-- Phase 6: Advanced database features (Task B rubric row).
--
-- Not run automatically (spring.sql.init.mode=never; schema is managed by hand,
-- matching spring.jpa.hibernate.ddl-auto=validate). Apply once per database with:
--   mysql -u <user> -p sunrise_dental < src/main/resources/db/phase6-advanced-db-objects.sql
--
-- If your MySQL server has binary logging enabled, creating the function below
-- requires either SUPER/SYSTEM_VARIABLES_ADMIN privilege plus:
--   SET GLOBAL log_bin_trust_function_creators = 1;
-- or connecting as a user who already has that trust flag set.

-- ---------------------------------------------------------------------------
-- FUNCTION: fn_patient_loyalty_discount_rate
--
-- Returns the loyalty discount rate to apply when billing a patient, based on
-- how many COMPLETED appointments already exist for their contact number.
-- This is a database-side re-implementation of the same rule enforced in Java
-- by ReturningPatientDiscountStrategy/DiscountStrategyFactory (Phase 4), so the
-- two can be cross-checked. Called from the app via a native @Query in
-- AppointmentRepository (see findLoyaltyDiscountRate).
-- ---------------------------------------------------------------------------
DROP FUNCTION IF EXISTS fn_patient_loyalty_discount_rate;

DELIMITER $$

CREATE FUNCTION fn_patient_loyalty_discount_rate(p_contact_number VARCHAR(30))
RETURNS DECIMAL(4,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE completed_count INT DEFAULT 0;

    SELECT COUNT(*) INTO completed_count
    FROM appointments a
    JOIN patients p ON p.id = a.patient_id
    WHERE p.contact_number = p_contact_number
      AND a.status = 'COMPLETED';

    IF completed_count > 0 THEN
        RETURN 0.05;
    ELSE
        RETURN 0.00;
    END IF;
END$$

DELIMITER ;

-- ---------------------------------------------------------------------------
-- PROCEDURE: sp_appointment_history
--
-- Returns every appointment (any status), most recent first, for whichever
-- patient row(s) share the given contact number. Lets the front desk pull a
-- patient's visit history from a phone number alone, without an appointment
-- number. Called from the app via plain JDBC (AppointmentHistoryJdbcDao),
-- deliberately bypassing Hibernate/JPA to demonstrate a direct stored
-- procedure call.
-- ---------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_appointment_history;

DELIMITER $$

CREATE PROCEDURE sp_appointment_history(IN p_contact_number VARCHAR(30))
BEGIN
    SELECT
        a.appointment_number,
        a.dentist_name,
        a.appointment_at,
        a.status,
        t.name AS treatment_name,
        t.base_cost AS treatment_cost
    FROM appointments a
    JOIN patients p ON p.id = a.patient_id
    JOIN treatments t ON t.id = a.treatment_id
    WHERE p.contact_number = p_contact_number
    ORDER BY a.appointment_at DESC;
END$$

DELIMITER ;

-- ---------------------------------------------------------------------------
-- TRIGGER: trg_prevent_dentist_double_booking
--
-- Business rule: a dentist cannot have two non-cancelled appointments at the
-- exact same date/time. AppointmentServiceImpl already checks this in Java
-- before saving, but this trigger enforces it as a hard backstop at the
-- database layer — e.g. against a second write path, a raw SQL insert, or a
-- race between two concurrent requests that both pass the Java check before
-- either has committed. Fires BEFORE INSERT only, since nothing in the app
-- currently updates an existing appointment's dentist/time (no reschedule
-- feature yet).
-- ---------------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_prevent_dentist_double_booking;

DELIMITER $$

CREATE TRIGGER trg_prevent_dentist_double_booking
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    DECLARE conflict_count INT DEFAULT 0;

    SELECT COUNT(*) INTO conflict_count
    FROM appointments
    WHERE LOWER(dentist_name) = LOWER(NEW.dentist_name)
      AND appointment_at = NEW.appointment_at
      AND status <> 'CANCELLED';

    IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Dentist already has an appointment at this date and time';
    END IF;
END$$

DELIMITER ;
