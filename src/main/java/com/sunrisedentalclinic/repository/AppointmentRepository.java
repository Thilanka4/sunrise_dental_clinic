package com.sunrisedentalclinic.repository;

import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    boolean existsByDentistNameIgnoreCaseAndAppointmentAtAndStatusNot(
            String dentistName, LocalDateTime appointmentAt, AppointmentStatus excludedStatus);

    long countByPatient_ContactNumberAndStatus(String contactNumber, AppointmentStatus status);
}
