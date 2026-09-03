package com.sunrisedentalclinic.dto;

import com.sunrisedentalclinic.model.AppointmentStatus;

import java.time.LocalDateTime;

/** One row of the dashboard's "today's appointments" report. */
public record TodayAppointmentEntry(
        String appointmentNumber,
        String patientName,
        String dentistName,
        String treatmentName,
        LocalDateTime appointmentAt,
        AppointmentStatus status) {
}
