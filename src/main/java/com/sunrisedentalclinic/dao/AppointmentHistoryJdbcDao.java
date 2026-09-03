package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.dto.PatientAppointmentHistoryEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Calls the {@code sp_appointment_history} stored procedure (see
 * src/main/resources/db/phase6-advanced-db-objects.sql) directly via plain JDBC —
 * deliberately bypassing Hibernate/JPA — to demonstrate invoking a database stored
 * procedure from the application, per Task B's advanced database features rubric.
 */
@Repository
public class AppointmentHistoryJdbcDao {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentHistoryJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PatientAppointmentHistoryEntry> findByContactNumber(String contactNumber) {
        return jdbcTemplate.query(
                "{call sp_appointment_history(?)}",
                ps -> ps.setString(1, contactNumber),
                (rs, rowNum) -> new PatientAppointmentHistoryEntry(
                        rs.getString("appointment_number"),
                        rs.getString("dentist_name"),
                        rs.getTimestamp("appointment_at").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getString("treatment_name"),
                        rs.getBigDecimal("treatment_cost")));
    }
}
