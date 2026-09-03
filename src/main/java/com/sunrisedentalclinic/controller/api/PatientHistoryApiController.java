package com.sunrisedentalclinic.controller.api;

import com.sunrisedentalclinic.dao.AppointmentHistoryJdbcDao;
import com.sunrisedentalclinic.dto.PatientAppointmentHistoryEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientHistoryApiController {

    private final AppointmentHistoryJdbcDao appointmentHistoryJdbcDao;

    public PatientHistoryApiController(AppointmentHistoryJdbcDao appointmentHistoryJdbcDao) {
        this.appointmentHistoryJdbcDao = appointmentHistoryJdbcDao;
    }

    @GetMapping("/{contactNumber}/appointments")
    public ResponseEntity<List<PatientAppointmentHistoryEntry>> history(@PathVariable String contactNumber) {
        return ResponseEntity.ok(appointmentHistoryJdbcDao.findByContactNumber(contactNumber));
    }
}
