package com.sunrisedentalclinic.service.notification;

import com.sunrisedentalclinic.model.Appointment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Subject in the observer relationship: holds the registered {@link AppointmentObserver}s
 * and notifies each of them when an appointment is booked. Spring supplies the observer
 * list at construction time by collecting every {@code AppointmentObserver} bean.
 */
@Component
public class AppointmentBookingNotifier {

    private final List<AppointmentObserver> observers;

    public AppointmentBookingNotifier(List<AppointmentObserver> observers) {
        this.observers = observers;
    }

    public void notifyObservers(Appointment appointment) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentBooked(appointment);
        }
    }
}
