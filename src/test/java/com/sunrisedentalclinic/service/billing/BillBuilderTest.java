package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.Bill;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BillBuilderTest {

    private final Treatment treatment = new Treatment("General Checkup", "Routine exam", new BigDecimal("1500.00"));
    private final Patient patient = new Patient("Nimal Perera", "12 Lake Road", "0771234567");
    private final Appointment appointment = new Appointment("APT000001", patient, treatment, "Silva",
            LocalDateTime.now().plusDays(1));

    @Test
    void build_withNoDiscount_totalIsFullSubtotal() {
        Bill bill = new BillBuilder(appointment)
                .consultationFee(new BigDecimal("1000.00"))
                .treatmentCost(new BigDecimal("500.00"))
                .discountStrategy(new NoDiscountStrategy())
                .build();

        assertThat(bill.getTotalCost()).isEqualByComparingTo("1500.00");
        assertThat(bill.getAppointment()).isSameAs(appointment);
    }

    @Test
    void build_withReturningPatientDiscount_appliesFivePercentOff() {
        Bill bill = new BillBuilder(appointment)
                .consultationFee(new BigDecimal("1000.00"))
                .treatmentCost(new BigDecimal("500.00"))
                .discountStrategy(new ReturningPatientDiscountStrategy())
                .build();

        assertThat(bill.getTotalCost()).isEqualByComparingTo("1425.00");
    }

    @Test
    void build_withoutExplicitDiscountStrategy_defaultsToNoDiscount() {
        Bill bill = new BillBuilder(appointment)
                .consultationFee(new BigDecimal("1000.00"))
                .treatmentCost(new BigDecimal("500.00"))
                .build();

        assertThat(bill.getTotalCost()).isEqualByComparingTo("1500.00");
    }

    @Test
    void build_withoutExplicitIssuedAt_defaultsToNow() {
        LocalDateTime before = LocalDateTime.now();
        Bill bill = new BillBuilder(appointment).consultationFee(BigDecimal.TEN).treatmentCost(BigDecimal.TEN).build();
        LocalDateTime after = LocalDateTime.now();

        assertThat(bill.getIssuedAt()).isBetween(before, after);
    }

    @Test
    void build_withExplicitIssuedAt_usesGivenValue() {
        LocalDateTime issuedAt = LocalDateTime.of(2026, 1, 1, 9, 0);
        Bill bill = new BillBuilder(appointment)
                .consultationFee(BigDecimal.TEN)
                .treatmentCost(BigDecimal.TEN)
                .issuedAt(issuedAt)
                .build();

        assertThat(bill.getIssuedAt()).isEqualTo(issuedAt);
    }

    @Test
    void constructor_nullAppointment_throwsNullPointerException() {
        assertThatThrownBy(() -> new BillBuilder(null)).isInstanceOf(NullPointerException.class);
    }
}
