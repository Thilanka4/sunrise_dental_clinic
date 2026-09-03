package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.dto.BillResponse;
import com.sunrisedentalclinic.exception.DuplicateBillException;
import com.sunrisedentalclinic.exception.InvalidAppointmentStateException;
import com.sunrisedentalclinic.exception.ResourceNotFoundException;
import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.AppointmentStatus;
import com.sunrisedentalclinic.model.Bill;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import com.sunrisedentalclinic.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private BillRepository billRepository;
    @Mock
    private DiscountStrategyFactory discountStrategyFactory;

    private BillServiceImpl billService;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        billService = new BillServiceImpl(appointmentRepository, billRepository, discountStrategyFactory);

        Treatment treatment = new Treatment("General Checkup", "Routine exam", new BigDecimal("1500.00"));
        Patient patient = new Patient("Nimal Perera", "12 Lake Road", "0771234567");
        appointment = new Appointment("APT000001", patient, treatment, "Silva", LocalDateTime.now().plusDays(1));
    }

    @Test
    void generateBill_success_buildsSavesAndCompletesAppointment() {
        when(appointmentRepository.findByAppointmentNumber("APT000001")).thenReturn(Optional.of(appointment));
        when(billRepository.findByAppointmentId(any())).thenReturn(Optional.empty());
        when(discountStrategyFactory.resolve(any(), any())).thenReturn(new NoDiscountStrategy());
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BillResponse response = billService.generateBill("APT000001");

        assertThat(response.consultationFee()).isEqualByComparingTo("1000.00");
        assertThat(response.treatmentCost()).isEqualByComparingTo("1500.00");
        assertThat(response.discountAmount()).isEqualByComparingTo("0.00");
        assertThat(response.totalCost()).isEqualByComparingTo("2500.00");
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void generateBill_appointmentNotFound_throwsResourceNotFoundException() {
        when(appointmentRepository.findByAppointmentNumber("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billService.generateBill("MISSING"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void generateBill_cancelledAppointment_throwsInvalidAppointmentStateException() {
        ReflectionTestUtils.setField(appointment, "status", AppointmentStatus.CANCELLED);
        when(appointmentRepository.findByAppointmentNumber("APT000001")).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> billService.generateBill("APT000001"))
                .isInstanceOf(InvalidAppointmentStateException.class);
    }

    @Test
    void generateBill_alreadyBilled_throwsDuplicateBillException() {
        when(appointmentRepository.findByAppointmentNumber("APT000001")).thenReturn(Optional.of(appointment));
        when(billRepository.findByAppointmentId(any())).thenReturn(Optional.of(
                new Bill(appointment, new BigDecimal("1000.00"), new BigDecimal("1500.00"),
                        new BigDecimal("2500.00"), LocalDateTime.now())));

        assertThatThrownBy(() -> billService.generateBill("APT000001"))
                .isInstanceOf(DuplicateBillException.class);
    }

    @Test
    void findByAppointmentNumber_appointmentNotFound_throwsResourceNotFoundException() {
        when(appointmentRepository.findByAppointmentNumber("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billService.findByAppointmentNumber("MISSING"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByAppointmentNumber_noBillYet_throwsResourceNotFoundException() {
        when(appointmentRepository.findByAppointmentNumber("APT000001")).thenReturn(Optional.of(appointment));
        when(billRepository.findByAppointmentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billService.findByAppointmentNumber("APT000001"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByAppointmentNumber_billExists_returnsMappedResponse() {
        Bill bill = new Bill(appointment, new BigDecimal("1000.00"), new BigDecimal("1500.00"),
                new BigDecimal("2375.00"), LocalDateTime.of(2026, 1, 1, 9, 0));
        when(appointmentRepository.findByAppointmentNumber("APT000001")).thenReturn(Optional.of(appointment));
        when(billRepository.findByAppointmentId(any())).thenReturn(Optional.of(bill));

        BillResponse response = billService.findByAppointmentNumber("APT000001");

        assertThat(response.discountAmount()).isEqualByComparingTo("125.00");
        assertThat(response.totalCost()).isEqualByComparingTo("2375.00");
    }
}
