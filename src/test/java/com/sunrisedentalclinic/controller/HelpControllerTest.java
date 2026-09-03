package com.sunrisedentalclinic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/** Verifies FR5: the Help page is reachable and contains the staff guidance sections. */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "test-staff")
class HelpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void help_returnsGuidancePage() throws Exception {
        mockMvc.perform(get("/help"))
                .andExpect(status().isOk())
                .andExpect(view().name("help"))
                .andExpect(content().string(containsString("Signing in")));
    }
}
