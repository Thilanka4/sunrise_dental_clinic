package com.sunrisedentalclinic.dto;

import com.sunrisedentalclinic.model.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read-only projection of an appointment returned by both the REST API and the
 * web views, keeping the two presentation layers backed by identical data.
 */
public record AppointmentResponse(
        String appointmentNumber,
        String patientName,
        String address,
        String contactNumber,
        String dentistName,
        String treatmentName,
        BigDecimal treatmentCost,
        LocalDateTime appointmentAt,
        AppointmentStatus status) {
}
