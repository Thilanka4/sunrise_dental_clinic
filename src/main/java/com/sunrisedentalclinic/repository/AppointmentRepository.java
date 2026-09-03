package com.sunrisedentalclinic.repository;

import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    boolean existsByDentistNameIgnoreCaseAndAppointmentAtAndStatusNot(
            String dentistName, LocalDateTime appointmentAt, AppointmentStatus excludedStatus);

    /**
     * Calls the {@code fn_patient_loyalty_discount_rate} database function (see
     * src/main/resources/db/phase6-advanced-db-objects.sql) via a native query, rather
     * than recomputing the rule in Java, to demonstrate invoking a database function
     * from the application per Task B's advanced database features rubric.
     */
    @Query(value = "SELECT fn_patient_loyalty_discount_rate(:contactNumber)", nativeQuery = true)
    BigDecimal findLoyaltyDiscountRate(@Param("contactNumber") String contactNumber);
}
