package com.sunrisedentalclinic.dto;

import java.math.BigDecimal;

/**
 * Read-only projection of a treatment used to populate selection lists.
 */
public record TreatmentSummary(Long id, String name, String description, BigDecimal baseCost) {
}
