package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.model.AppointmentStatus;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Chooses the {@link DiscountStrategy} that applies to a bill, based on the patient's
 * appointment history and the treatment being billed. Returning-patient loyalty takes
 * precedence over the premium-treatment promotion; a patient with no discount
 * entitlement is billed in full.
 *
 * <p>Registration creates a new {@code Patient} row per appointment (there is no
 * patient-matching step), so history is looked up by contact number rather than
 * patient id — the one identifier that reliably ties appointments back to the same
 * person today.
 */
@Component
public class DiscountStrategyFactory {

    private static final BigDecimal PREMIUM_TREATMENT_THRESHOLD = new BigDecimal("10000.00");

    private final AppointmentRepository appointmentRepository;

    public DiscountStrategyFactory(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public DiscountStrategy resolve(Patient patient, Treatment treatment) {
        long completedAppointments = appointmentRepository.countByPatient_ContactNumberAndStatus(
                patient.getContactNumber(), AppointmentStatus.COMPLETED);
        if (completedAppointments > 0) {
            return new ReturningPatientDiscountStrategy();
        }
        if (treatment.getBaseCost().compareTo(PREMIUM_TREATMENT_THRESHOLD) >= 0) {
            return new PremiumTreatmentDiscountStrategy();
        }
        return new NoDiscountStrategy();
    }
}
