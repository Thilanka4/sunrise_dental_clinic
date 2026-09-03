package com.sunrisedentalclinic.service.notification;

import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class AppointmentBookingNotifierTest {

    private final Treatment treatment = new Treatment("General Checkup", "desc", new BigDecimal("1500.00"));
    private final Patient patient = new Patient("Nimal Perera", "12 Lake Road", "0771234567");
    private final Appointment appointment = new Appointment("APT000001", patient, treatment, "Silva",
            LocalDateTime.now().plusDays(1));

    @Test
    void notifyObservers_callsEveryRegisteredObserverExactlyOnce() {
        AppointmentObserver first = mock(AppointmentObserver.class);
        AppointmentObserver second = mock(AppointmentObserver.class);
        AppointmentBookingNotifier notifier = new AppointmentBookingNotifier(List.of(first, second));

        notifier.notifyObservers(appointment);

        verify(first, times(1)).onAppointmentBooked(appointment);
        verify(second, times(1)).onAppointmentBooked(appointment);
    }

    @Test
    void notifyObservers_noObservers_doesNotThrow() {
        AppointmentObserver unrelated = mock(AppointmentObserver.class);
        AppointmentBookingNotifier notifier = new AppointmentBookingNotifier(List.of());

        notifier.notifyObservers(appointment);

        verifyNoInteractions(unrelated);
    }
}
