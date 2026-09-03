package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.dto.BillResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BillPdfGeneratorTest {

    @Test
    void generate_producesNonEmptyPdfDocument() {
        BillResponse bill = new BillResponse(
                "APT000001", "Nimal Perera", "General Checkup",
                new BigDecimal("1000.00"), new BigDecimal("500.00"), BigDecimal.ZERO,
                new BigDecimal("1500.00"), LocalDateTime.of(2026, 1, 1, 9, 0));

        byte[] pdf = new BillPdfGenerator().generate(bill);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }
}
