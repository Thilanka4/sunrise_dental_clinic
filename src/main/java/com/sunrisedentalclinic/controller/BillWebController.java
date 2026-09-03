package com.sunrisedentalclinic.controller;

import com.sunrisedentalclinic.exception.DuplicateBillException;
import com.sunrisedentalclinic.exception.InvalidAppointmentStateException;
import com.sunrisedentalclinic.exception.ResourceNotFoundException;
import com.sunrisedentalclinic.service.billing.BillService;
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

    public BillWebController(BillService billService) {
        this.billService = billService;
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
}
