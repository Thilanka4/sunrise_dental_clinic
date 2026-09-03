package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dto.DashboardView;
import com.sunrisedentalclinic.dto.TodayAppointmentEntry;
import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import com.sunrisedentalclinic.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public DashboardServiceImpl(AppointmentRepository appointmentRepository, BillRepository billRepository) {
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardView loadDashboard() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        var todaysAppointments = appointmentRepository
                .findByAppointmentAtGreaterThanEqualAndAppointmentAtLessThanOrderByAppointmentAtAsc(
                        startOfToday, startOfTomorrow)
                .stream()
                .map(this::toTodayEntry)
                .toList();

        return new DashboardView(todaysAppointments, billRepository.findRevenueByTreatment());
    }

    private TodayAppointmentEntry toTodayEntry(Appointment appointment) {
        return new TodayAppointmentEntry(
                appointment.getAppointmentNumber(),
                appointment.getPatient().getFullName(),
                appointment.getDentistName(),
                appointment.getTreatment().getName(),
                appointment.getAppointmentAt(),
                appointment.getStatus());
    }
}
