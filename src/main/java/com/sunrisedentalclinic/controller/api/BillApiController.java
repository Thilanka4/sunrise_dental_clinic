package com.sunrisedentalclinic.controller.api;

import com.sunrisedentalclinic.dto.BillResponse;
import com.sunrisedentalclinic.service.billing.BillPdfGenerator;
import com.sunrisedentalclinic.service.billing.BillService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final BillPdfGenerator billPdfGenerator;

    public BillApiController(BillService billService, BillPdfGenerator billPdfGenerator) {
        this.billService = billService;
        this.billPdfGenerator = billPdfGenerator;
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

    @GetMapping(value = "/{appointmentNumber}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdf(@PathVariable String appointmentNumber) {
        BillResponse bill = billService.findByAppointmentNumber(appointmentNumber);
        byte[] pdf = billPdfGenerator.generate(bill);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + appointmentNumber + "-bill.pdf\"")
                .body(pdf);
    }
}
