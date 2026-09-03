package com.sunrisedentalclinic.service.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Loyalty discount for patients who have completed at least one prior appointment. */
public class ReturningPatientDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.05");

    @Override
    public BigDecimal apply(BigDecimal subtotal) {
        BigDecimal discount = subtotal.multiply(RATE).setScale(2, RoundingMode.HALF_UP);
        return subtotal.subtract(discount);
    }

    @Override
    public String getDescription() {
        return "Returning patient discount (5%)";
    }
}
