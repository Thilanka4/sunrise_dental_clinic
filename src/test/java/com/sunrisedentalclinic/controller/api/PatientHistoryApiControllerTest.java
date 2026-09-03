package com.sunrisedentalclinic.controller.api;

import com.sunrisedentalclinic.dao.AppointmentHistoryJdbcDao;
import com.sunrisedentalclinic.dto.PatientAppointmentHistoryEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack integration test against the real app context, except
 * {@link AppointmentHistoryJdbcDao}: it calls the MySQL-only sp_appointment_history
 * stored procedure (Phase 6), which doesn't exist in H2, so it's mocked here.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "test-staff")
class PatientHistoryApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentHistoryJdbcDao appointmentHistoryJdbcDao;

    @Test
    void history_returnsRowsFromStoredProcedureCall() throws Exception {
        when(appointmentHistoryJdbcDao.findByContactNumber("0771234567")).thenReturn(List.of(
                new PatientAppointmentHistoryEntry("APT000001", "Silva",
                        LocalDateTime.of(2026, 1, 1, 9, 0), "COMPLETED",
                        "General Checkup", new BigDecimal("1500.00"))));

        mockMvc.perform(get("/api/patients/{contactNumber}/appointments", "0771234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentNumber").value("APT000001"))
                .andExpect(jsonPath("$[0].dentistName").value("Silva"));
    }

    @Test
    void history_noAppointments_returnsEmptyArray() throws Exception {
        when(appointmentHistoryJdbcDao.findByContactNumber("0000000000")).thenReturn(List.of());

        mockMvc.perform(get("/api/patients/{contactNumber}/appointments", "0000000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
