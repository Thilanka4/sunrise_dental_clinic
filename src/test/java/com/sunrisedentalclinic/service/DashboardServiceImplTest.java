package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dto.DashboardView;
import com.sunrisedentalclinic.dto.TreatmentRevenueEntry;
import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import com.sunrisedentalclinic.repository.BillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private BillRepository billRepository;

    @Test
    void loadDashboard_boundsAppointmentQueryToTodayAndBundlesRevenue() {
        Treatment treatment = new Treatment("General Checkup", "Routine exam", new BigDecimal("1500.00"));
        Patient patient = new Patient("Nimal Perera", "12 Lake Road", "0771234567");
        Appointment appointment = new Appointment("APT000001", patient, treatment, "Silva",
                LocalDate.now().atTime(10, 0));

        when(appointmentRepository.findByAppointmentAtGreaterThanEqualAndAppointmentAtLessThanOrderByAppointmentAtAsc(
                any(), any())).thenReturn(List.of(appointment));
        List<TreatmentRevenueEntry> revenue = List.of(
                new TreatmentRevenueEntry("General Checkup", new BigDecimal("1500.00")));
        when(billRepository.findRevenueByTreatment()).thenReturn(revenue);

        DashboardView view = new DashboardServiceImpl(appointmentRepository, billRepository).loadDashboard();

        assertThat(view.todaysAppointments()).hasSize(1);
        assertThat(view.todaysAppointments().get(0).appointmentNumber()).isEqualTo("APT000001");
        assertThat(view.revenueByTreatment()).isEqualTo(revenue);

        ArgumentCaptor<LocalDateTime> start = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> end = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(appointmentRepository).findByAppointmentAtGreaterThanEqualAndAppointmentAtLessThanOrderByAppointmentAtAsc(
                start.capture(), end.capture());
        assertThat(start.getValue()).isEqualTo(LocalDate.now().atStartOfDay());
        assertThat(end.getValue()).isEqualTo(LocalDate.now().plusDays(1).atStartOfDay());
    }
}
