package com.sunrisedentalclinic.controller.api;

import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.AppointmentStatus;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import com.sunrisedentalclinic.repository.PatientRepository;
import com.sunrisedentalclinic.repository.TreatmentRepository;
import com.sunrisedentalclinic.service.billing.DiscountStrategyFactory;
import com.sunrisedentalclinic.service.billing.NoDiscountStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack integration test against the real app context + H2, except for
 * {@link DiscountStrategyFactory}: it calls the MySQL-only fn_patient_loyalty_discount_rate
 * function (Phase 6), which doesn't exist in H2, so it's the one collaborator mocked here.
 * Everything else — BillServiceImpl, BillBuilder, the discount strategies, real
 * repositories/persistence — runs for real.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test-staff")
class BillApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private TreatmentRepository treatmentRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @MockitoBean
    private DiscountStrategyFactory discountStrategyFactory;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        when(discountStrategyFactory.resolve(any(), any())).thenReturn(new NoDiscountStrategy());

        // A distinct name is required: TreatmentDataInitializer seeds "General Checkup" et al.
        // into this shared H2 context once at startup, and Treatment.name is unique.
        Treatment treatment = treatmentRepository.save(
                new Treatment("QA Bill Test Treatment", "Routine exam", new BigDecimal("1500.00")));
        Patient patient = patientRepository.save(new Patient("Nimal Perera", "12 Lake Road", "0771234567"));
        appointment = appointmentRepository.save(
                new Appointment("APT000001", patient, treatment, "Silva", LocalDateTime.now().plusDays(1)));
    }

    @Test
    void generateBill_success_returns201AndMarksAppointmentCompleted() throws Exception {
        mockMvc.perform(post("/api/bills/{number}", appointment.getAppointmentNumber()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.consultationFee").value(1000.00))
                .andExpect(jsonPath("$.treatmentCost").value(1500.00))
                .andExpect(jsonPath("$.discountAmount").value(0))
                .andExpect(jsonPath("$.totalCost").value(2500.00));

        Appointment reloaded = appointmentRepository.findById(appointment.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void generateBill_calledTwice_secondCallReturns409() throws Exception {
        mockMvc.perform(post("/api/bills/{number}", appointment.getAppointmentNumber()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/bills/{number}", appointment.getAppointmentNumber()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Bill already exists"));
    }

    @Test
    void generateBill_unknownAppointment_returns404() throws Exception {
        mockMvc.perform(post("/api/bills/{number}", "APT999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBill_beforeGeneration_returns404() throws Exception {
        mockMvc.perform(get("/api/bills/{number}", appointment.getAppointmentNumber()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBill_afterGeneration_returnsSameBill() throws Exception {
        mockMvc.perform(post("/api/bills/{number}", appointment.getAppointmentNumber()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/bills/{number}", appointment.getAppointmentNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCost").value(2500.00));
    }
}
