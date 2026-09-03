package com.sunrisedentalclinic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the real Spring Security form-login/logout flow (no {@code @WithMockUser}
 * shortcut) against the admin user seeded by StaffUserDataInitializer, covering
 * FR1 (login) and FR6 (safely exiting the system, i.e. logout).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_validCredentials_redirectsToDashboard() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("ChangeMe123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void login_invalidCredentials_redirectsBackToLoginWithError() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("WrongPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void logout_endsSessionAndRedirectsToLoginWithLogoutFlag() throws Exception {
        mockMvc.perform(logout())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void unauthenticatedRequest_toProtectedPage_isRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }
}
