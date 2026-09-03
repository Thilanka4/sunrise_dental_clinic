package com.sunrisedentalclinic.controller;

import com.sunrisedentalclinic.dto.BillResponse;
import com.sunrisedentalclinic.exception.DuplicateBillException;
import com.sunrisedentalclinic.exception.InvalidAppointmentStateException;
import com.sunrisedentalclinic.exception.ResourceNotFoundException;
import com.sunrisedentalclinic.service.billing.BillPdfGenerator;
import com.sunrisedentalclinic.service.billing.BillService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments/{appointmentNumber}/bill")
public class BillWebController {

    private final BillService billService;
    private final BillPdfGenerator billPdfGenerator;

    public BillWebController(BillService billService, BillPdfGenerator billPdfGenerator) {
        this.billService = billService;
        this.billPdfGenerator = billPdfGenerator;
    }

    @PostMapping
    public String generate(@PathVariable String appointmentNumber, RedirectAttributes redirectAttributes) {
        try {
            billService.generateBill(appointmentNumber);
        } catch (DuplicateBillException ignored) {
            // A bill already exists; fall through to show it.
        } catch (InvalidAppointmentStateException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("billError", ex.getMessage());
            return "redirect:/appointments/" + appointmentNumber;
        }
        return "redirect:/appointments/" + appointmentNumber + "/bill";
    }

    @GetMapping
    public String view(@PathVariable String appointmentNumber, Model model) {
        try {
            model.addAttribute("bill", billService.findByAppointmentNumber(appointmentNumber));
            return "bill-details";
        } catch (ResourceNotFoundException ex) {
            return "redirect:/appointments/" + appointmentNumber;
        }
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String appointmentNumber) {
        BillResponse bill;
        try {
            bill = billService.findByAppointmentNumber(appointmentNumber);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdf = billPdfGenerator.generate(bill);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + appointmentNumber + "-bill.pdf\"")
                .body(pdf);
    }
}
