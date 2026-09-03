package com.sunrisedentalclinic.controller;

import com.sunrisedentalclinic.dto.AppointmentRegistrationRequest;
import com.sunrisedentalclinic.dto.AppointmentResponse;
import com.sunrisedentalclinic.exception.DoubleBookingException;
import com.sunrisedentalclinic.exception.ResourceNotFoundException;
import com.sunrisedentalclinic.service.AppointmentService;
import com.sunrisedentalclinic.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/appointments")
public class AppointmentWebController {

    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;

    public AppointmentWebController(AppointmentService appointmentService, TreatmentService treatmentService) {
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
    }

    @GetMapping("/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("registrationRequest", new AppointmentRegistrationRequest());
        model.addAttribute("treatments", treatmentService.listAll());
        return "appointment-form";
    }

    @PostMapping
    public String registerAppointment(
            @Valid @ModelAttribute("registrationRequest") AppointmentRegistrationRequest request,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("treatments", treatmentService.listAll());
            return "appointment-form";
        }
        try {
            AppointmentResponse response = appointmentService.registerAppointment(request);
            return "redirect:/appointments/" + response.appointmentNumber();
        } catch (DoubleBookingException ex) {
            bindingResult.reject("doubleBooking", ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            bindingResult.rejectValue("treatmentId", "invalid", ex.getMessage());
        }
        model.addAttribute("treatments", treatmentService.listAll());
        return "appointment-form";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "appointmentNumber", required = false) String appointmentNumber,
            Model model) {
        if (appointmentNumber == null || appointmentNumber.isBlank()) {
            return "appointment-search";
        }
        try {
            appointmentService.findByAppointmentNumber(appointmentNumber);
            return "redirect:/appointments/" + appointmentNumber;
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("notFound", true);
            model.addAttribute("searchedNumber", appointmentNumber);
            return "appointment-search";
        }
    }

    @GetMapping("/{appointmentNumber}")
    public String details(@PathVariable String appointmentNumber, Model model) {
        try {
            model.addAttribute("appointment", appointmentService.findByAppointmentNumber(appointmentNumber));
            return "appointment-details";
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("notFound", true);
            model.addAttribute("searchedNumber", appointmentNumber);
            return "appointment-search";
        }
    }
}
