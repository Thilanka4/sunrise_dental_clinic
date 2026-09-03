package com.sunrisedentalclinic.service.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Promotional discount encouraging uptake of higher-cost treatments. */
public class PremiumTreatmentDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.07");

    @Override
    public BigDecimal apply(BigDecimal subtotal) {
        BigDecimal discount = subtotal.multiply(RATE).setScale(2, RoundingMode.HALF_UP);
        return subtotal.subtract(discount);
    }

    @Override
    public String getDescription() {
        return "Premium treatment discount (7%)";
    }
}
