package com.sunrisedentalclinic.service.billing;

import java.math.BigDecimal;

/** A pluggable rule for discounting a bill subtotal. */
public interface DiscountStrategy {

    BigDecimal apply(BigDecimal subtotal);

    String getDescription();
}
