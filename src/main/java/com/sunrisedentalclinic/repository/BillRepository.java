package com.sunrisedentalclinic.repository;

import com.sunrisedentalclinic.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByAppointmentId(Long appointmentId);
}
