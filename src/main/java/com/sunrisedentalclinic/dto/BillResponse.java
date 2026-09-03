package com.sunrisedentalclinic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Read-only projection of a bill returned by both the REST API and the web views. */
public record BillResponse(
        String appointmentNumber,
        String patientName,
        String treatmentName,
        BigDecimal consultationFee,
        BigDecimal treatmentCost,
        BigDecimal discountAmount,
        BigDecimal totalCost,
        LocalDateTime issuedAt) {
}
