package com.sunrisedentalclinic.dto;

import java.util.List;

/** The two Phase 7 reports, bundled together for the staff dashboard page. */
public record DashboardView(
        List<TodayAppointmentEntry> todaysAppointments,
        List<TreatmentRevenueEntry> revenueByTreatment) {
}
