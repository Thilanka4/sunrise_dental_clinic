package com.sunrisedentalclinic.repository;

import com.sunrisedentalclinic.dto.TreatmentRevenueEntry;
import com.sunrisedentalclinic.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByAppointmentId(Long appointmentId);

    @Query("SELECT new com.sunrisedentalclinic.dto.TreatmentRevenueEntry(b.appointment.treatment.name, SUM(b.totalCost)) "
            + "FROM Bill b GROUP BY b.appointment.treatment.name ORDER BY SUM(b.totalCost) DESC")
    List<TreatmentRevenueEntry> findRevenueByTreatment();
}
