package com.sunrisedentalclinic.service.billing;

import java.math.BigDecimal;

/** Default strategy: the subtotal is billed in full. */
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal apply(BigDecimal subtotal) {
        return subtotal;
    }

    @Override
    public String getDescription() {
        return "No discount";
    }
}
