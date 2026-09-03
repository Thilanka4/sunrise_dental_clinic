package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountStrategyFactoryTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    private final Patient patient = new Patient("Nimal Perera", "12 Lake Road", "0771234567");

    @Test
    void resolve_returningPatient_takesPrecedenceOverPremiumTreatment() {
        when(appointmentRepository.findLoyaltyDiscountRate("0771234567")).thenReturn(new BigDecimal("0.05"));
        Treatment premiumTreatment = new Treatment("Root Canal", "desc", new BigDecimal("15000.00"));

        DiscountStrategyFactory factory = new DiscountStrategyFactory(appointmentRepository);

        assertThat(factory.resolve(patient, premiumTreatment)).isInstanceOf(ReturningPatientDiscountStrategy.class);
    }

    @Test
    void resolve_firstTimePatient_premiumTreatment_returnsPremiumDiscount() {
        when(appointmentRepository.findLoyaltyDiscountRate("0771234567")).thenReturn(BigDecimal.ZERO);
        Treatment premiumTreatment = new Treatment("Root Canal", "desc", new BigDecimal("15000.00"));

        DiscountStrategyFactory factory = new DiscountStrategyFactory(appointmentRepository);

        assertThat(factory.resolve(patient, premiumTreatment)).isInstanceOf(PremiumTreatmentDiscountStrategy.class);
    }

    @Test
    void resolve_firstTimePatient_belowPremiumThreshold_returnsNoDiscount() {
        when(appointmentRepository.findLoyaltyDiscountRate("0771234567")).thenReturn(BigDecimal.ZERO);
        Treatment regularTreatment = new Treatment("General Checkup", "desc", new BigDecimal("1500.00"));

        DiscountStrategyFactory factory = new DiscountStrategyFactory(appointmentRepository);

        assertThat(factory.resolve(patient, regularTreatment)).isInstanceOf(NoDiscountStrategy.class);
    }

    @Test
    void resolve_treatmentCostExactlyAtThreshold_isTreatedAsPremium() {
        when(appointmentRepository.findLoyaltyDiscountRate("0771234567")).thenReturn(BigDecimal.ZERO);
        Treatment thresholdTreatment = new Treatment("Whitening", "desc", new BigDecimal("10000.00"));

        DiscountStrategyFactory factory = new DiscountStrategyFactory(appointmentRepository);

        assertThat(factory.resolve(patient, thresholdTreatment)).isInstanceOf(PremiumTreatmentDiscountStrategy.class);
    }

    @Test
    void resolve_nullLoyaltyRate_treatedAsNotReturning() {
        when(appointmentRepository.findLoyaltyDiscountRate("0771234567")).thenReturn(null);
        Treatment regularTreatment = new Treatment("General Checkup", "desc", new BigDecimal("1500.00"));

        DiscountStrategyFactory factory = new DiscountStrategyFactory(appointmentRepository);

        assertThat(factory.resolve(patient, regularTreatment)).isInstanceOf(NoDiscountStrategy.class);
    }
}
