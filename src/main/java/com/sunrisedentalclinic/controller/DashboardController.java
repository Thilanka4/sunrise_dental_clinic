package com.sunrisedentalclinic.controller;

import com.sunrisedentalclinic.dto.DashboardView;
import com.sunrisedentalclinic.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        DashboardView view = dashboardService.loadDashboard();
        model.addAttribute("todaysAppointments", view.todaysAppointments());
        model.addAttribute("revenueByTreatment", view.revenueByTreatment());
        return "home";
    }
}
