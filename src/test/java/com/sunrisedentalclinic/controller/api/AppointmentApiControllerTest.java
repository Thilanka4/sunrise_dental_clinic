package com.sunrisedentalclinic.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack integration test: real Spring context, real security config, and a real
 * (in-memory H2) database — see src/test/resources/application.properties. Registration
 * doesn't touch any of the MySQL-specific Phase 6 objects, so no mocking is needed here.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "test-staff")
class AppointmentApiControllerTest {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_validRequest_persistsAndReturns201() throws Exception {
        String body = registrationJson("Nimal Perera", "Fernando", LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentNumber", notNullValue()))
                .andExpect(jsonPath("$.patientName").value("Nimal Perera"))
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void register_blankPatientName_returns400WithFieldError() throws Exception {
        String body = registrationJson("", "Fernando", LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.patientName", notNullValue()));
    }

    @Test
    void register_sameDentistAndTimeTwice_secondRequestReturns409() throws Exception {
        LocalDateTime when = LocalDateTime.now().plusDays(2);
        String first = registrationJson("Patient One", "Costa", when);
        String second = registrationJson("Patient Two", "costa", when);

        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(first))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(second))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Booking conflict"));
    }

    @Test
    void get_unknownAppointmentNumber_returns404() throws Exception {
        mockMvc.perform(get("/api/appointments/APT999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerThenGet_returnsSameAppointment() throws Exception {
        String body = registrationJson("Kamal Silva", "Weerasinghe", LocalDateTime.now().plusDays(3));

        String response = mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String appointmentNumber = objectMapper.readTree(response).get("appointmentNumber").asText();

        mockMvc.perform(get("/api/appointments/" + appointmentNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dentistName").value("Weerasinghe"));
    }

    private String registrationJson(String patientName, String dentistName, LocalDateTime appointmentAt)
            throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "patientName", patientName,
                "address", "12 Lake Road",
                "contactNumber", "0771234567",
                "dentistName", dentistName,
                "treatmentId", 1L,
                "appointmentAt", appointmentAt.format(FORMAT)));
    }
}
