package com.sunrisedentalclinic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One row of a patient's appointment history, as returned by {@code sp_appointment_history}. */
public record PatientAppointmentHistoryEntry(
        String appointmentNumber,
        String dentistName,
        LocalDateTime appointmentAt,
        String status,
        String treatmentName,
        BigDecimal treatmentCost) {
}
