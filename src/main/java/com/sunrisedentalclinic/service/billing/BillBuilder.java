package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Assembles a {@link Bill} step by step, applying the chosen {@link DiscountStrategy}
 * to the consultation fee and treatment cost to derive the total at {@link #build()}
 * time, rather than requiring the caller to compute it up front.
 */
public class BillBuilder {

    private final Appointment appointment;
    private BigDecimal consultationFee = BigDecimal.ZERO;
    private BigDecimal treatmentCost = BigDecimal.ZERO;
    private DiscountStrategy discountStrategy = new NoDiscountStrategy();
    private LocalDateTime issuedAt = LocalDateTime.now();

    public BillBuilder(Appointment appointment) {
        this.appointment = Objects.requireNonNull(appointment, "appointment is required");
    }

    public BillBuilder consultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
        return this;
    }

    public BillBuilder treatmentCost(BigDecimal treatmentCost) {
        this.treatmentCost = treatmentCost;
        return this;
    }

    public BillBuilder discountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
        return this;
    }

    public BillBuilder issuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
        return this;
    }

    public Bill build() {
        BigDecimal subtotal = consultationFee.add(treatmentCost);
        BigDecimal total = discountStrategy.apply(subtotal);
        return new Bill(appointment, consultationFee, treatmentCost, total, issuedAt);
    }
}
