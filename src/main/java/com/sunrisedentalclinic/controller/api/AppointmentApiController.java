package com.sunrisedentalclinic.controller.api;

import com.sunrisedentalclinic.dto.AppointmentRegistrationRequest;
import com.sunrisedentalclinic.dto.AppointmentResponse;
import com.sunrisedentalclinic.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    private final AppointmentService appointmentService;

    public AppointmentApiController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> register(@Valid @RequestBody AppointmentRegistrationRequest request) {
        AppointmentResponse response = appointmentService.registerAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{appointmentNumber}")
    public ResponseEntity<AppointmentResponse> get(@PathVariable String appointmentNumber) {
        return ResponseEntity.ok(appointmentService.findByAppointmentNumber(appointmentNumber));
    }
}
