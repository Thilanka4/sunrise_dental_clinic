package com.sunrisedentalclinic.service.billing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class NoDiscountStrategyTest {

    private final NoDiscountStrategy strategy = new NoDiscountStrategy();

    @Test
    void apply_returnsSubtotalUnchanged() {
        assertThat(strategy.apply(new BigDecimal("1234.56"))).isEqualByComparingTo("1234.56");
    }

    @Test
    void getDescription_isHumanReadable() {
        assertThat(strategy.getDescription()).isEqualTo("No discount");
    }
}
