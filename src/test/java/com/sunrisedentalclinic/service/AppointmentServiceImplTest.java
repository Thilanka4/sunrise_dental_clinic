package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dto.AppointmentRegistrationRequest;
import com.sunrisedentalclinic.dto.AppointmentResponse;
import com.sunrisedentalclinic.exception.DoubleBookingException;
import com.sunrisedentalclinic.exception.ResourceNotFoundException;
import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.AppointmentStatus;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import com.sunrisedentalclinic.repository.PatientRepository;
import com.sunrisedentalclinic.repository.TreatmentRepository;
import com.sunrisedentalclinic.service.notification.AppointmentBookingNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private TreatmentRepository treatmentRepository;
    @Mock
    private AppointmentNumberGenerator appointmentNumberGenerator;
    @Mock
    private AppointmentBookingNotifier appointmentBookingNotifier;

    private AppointmentServiceImpl appointmentService;
    private AppointmentRegistrationRequest request;
    private Treatment treatment;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentServiceImpl(appointmentRepository, patientRepository,
                treatmentRepository, appointmentNumberGenerator, appointmentBookingNotifier);

        treatment = new Treatment("General Checkup", "Routine exam", new BigDecimal("1500.00"));

        request = new AppointmentRegistrationRequest();
        request.setPatientName("Nimal Perera");
        request.setAddress("12 Lake Road");
        request.setContactNumber("0771234567");
        request.setDentistName("Silva");
        request.setTreatmentId(1L);
        request.setAppointmentAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void registerAppointment_savesAndNotifiesObserversOnSuccess() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistNameIgnoreCaseAndAppointmentAtAndStatusNot(
                anyString(), any(), any())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentNumberGenerator.next()).thenReturn("APT000001");
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentService.registerAppointment(request);

        assertThat(response.appointmentNumber()).isEqualTo("APT000001");
        assertThat(response.patientName()).isEqualTo("Nimal Perera");
        assertThat(response.treatmentName()).isEqualTo("General Checkup");
        assertThat(response.status()).isEqualTo(AppointmentStatus.BOOKED);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentBookingNotifier).notifyObservers(captor.capture());
        assertThat(captor.getValue().getAppointmentNumber()).isEqualTo("APT000001");
    }

    @Test
    void registerAppointment_treatmentNotFound_throwsResourceNotFoundException() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.registerAppointment(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(patientRepository, never()).save(any());
    }

    @Test
    void registerAppointment_dentistBusy_throwsDoubleBookingExceptionWithoutSaving() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistNameIgnoreCaseAndAppointmentAtAndStatusNot(
                anyString(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.registerAppointment(request))
                .isInstanceOf(DoubleBookingException.class);

        verify(patientRepository, never()).save(any());
        verify(appointmentBookingNotifier, never()).notifyObservers(any());
    }

    @Test
    void registerAppointment_dbTriggerRejectsInsert_translatedToDoubleBookingException() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistNameIgnoreCaseAndAppointmentAtAndStatusNot(
                anyString(), any(), any())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentNumberGenerator.next()).thenReturn("APT000001");
        when(appointmentRepository.save(any(Appointment.class))).thenThrow(
                new DataIntegrityViolationException("Dentist already has an appointment at this date and time"));

        assertThatThrownBy(() -> appointmentService.registerAppointment(request))
                .isInstanceOf(DoubleBookingException.class);
    }

    @Test
    void registerAppointment_unrelatedDataAccessException_propagatesUnchanged() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistNameIgnoreCaseAndAppointmentAtAndStatusNot(
                anyString(), any(), any())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentNumberGenerator.next()).thenReturn("APT000001");
        when(appointmentRepository.save(any(Appointment.class)))
                .thenThrow(new DataIntegrityViolationException("some other constraint violation"));

        assertThatThrownBy(() -> appointmentService.registerAppointment(request))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByAppointmentNumber_found_returnsResponse() {
        Patient patient = new Patient("Nimal Perera", "12 Lake Road", "0771234567");
        Appointment appointment = new Appointment("APT000001", patient, treatment, "Silva",
                LocalDateTime.now().plusDays(1));
        when(appointmentRepository.findByAppointmentNumber("APT000001")).thenReturn(Optional.of(appointment));

        AppointmentResponse response = appointmentService.findByAppointmentNumber("APT000001");

        assertThat(response.appointmentNumber()).isEqualTo("APT000001");
        assertThat(response.dentistName()).isEqualTo("Silva");
    }

    @Test
    void findByAppointmentNumber_notFound_throwsResourceNotFoundException() {
        when(appointmentRepository.findByAppointmentNumber("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findByAppointmentNumber("MISSING"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
