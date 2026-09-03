package com.sunrisedentalclinic.controller.api;

import com.sunrisedentalclinic.dto.BillResponse;
import com.sunrisedentalclinic.service.billing.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
public class BillApiController {

    private final BillService billService;

    public BillApiController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/{appointmentNumber}")
    public ResponseEntity<BillResponse> generate(@PathVariable String appointmentNumber) {
        BillResponse response = billService.generateBill(appointmentNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{appointmentNumber}")
    public ResponseEntity<BillResponse> get(@PathVariable String appointmentNumber) {
        return ResponseEntity.ok(billService.findByAppointmentNumber(appointmentNumber));
    }
}
