package com.sunrisedentalclinic.dto;

import java.math.BigDecimal;

/** One row of the dashboard's "revenue by treatment type" report. */
public record TreatmentRevenueEntry(String treatmentName, BigDecimal totalRevenue) {
}
