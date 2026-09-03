package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.dto.PatientAppointmentHistoryEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentHistoryJdbcDaoTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @SuppressWarnings("unchecked")
    void findByContactNumber_callsStoredProcedureAndReturnsMappedRows() {
        List<PatientAppointmentHistoryEntry> expected = List.of(new PatientAppointmentHistoryEntry(
                "APT000001", "Silva", LocalDateTime.of(2026, 1, 1, 9, 0), "COMPLETED",
                "General Checkup", new BigDecimal("1500.00")));

        when(jdbcTemplate.query(eq("{call sp_appointment_history(?)}"), any(PreparedStatementSetter.class),
                any(RowMapper.class))).thenReturn(expected);

        List<PatientAppointmentHistoryEntry> result =
                new AppointmentHistoryJdbcDao(jdbcTemplate).findByContactNumber("0771234567");

        assertThat(result).isEqualTo(expected);
    }
}
