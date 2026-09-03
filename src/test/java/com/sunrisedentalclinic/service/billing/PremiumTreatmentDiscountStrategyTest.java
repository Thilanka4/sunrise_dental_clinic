package com.sunrisedentalclinic.service.billing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PremiumTreatmentDiscountStrategyTest {

    private final PremiumTreatmentDiscountStrategy strategy = new PremiumTreatmentDiscountStrategy();

    @Test
    void apply_deducts7PercentFromSubtotal() {
        assertThat(strategy.apply(new BigDecimal("10000.00"))).isEqualByComparingTo("9300.00");
    }

    @Test
    void getDescription_mentionsSevenPercent() {
        assertThat(strategy.getDescription()).contains("7%");
    }
}
