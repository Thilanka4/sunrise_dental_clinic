package com.sunrisedentalclinic.service.billing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ReturningPatientDiscountStrategyTest {

    private final ReturningPatientDiscountStrategy strategy = new ReturningPatientDiscountStrategy();

    @Test
    void apply_deducts5PercentFromSubtotal() {
        assertThat(strategy.apply(new BigDecimal("1000.00"))).isEqualByComparingTo("950.00");
    }

    @Test
    void apply_roundsHalfUpToTwoDecimalPlaces() {
        assertThat(strategy.apply(new BigDecimal("1025.00"))).isEqualByComparingTo("973.75");
    }

    @Test
    void getDescription_mentionsFivePercent() {
        assertThat(strategy.getDescription()).contains("5%");
    }
}
