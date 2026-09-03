package com.sunrisedentalclinic.service.notification;

import com.sunrisedentalclinic.model.Appointment;

/** Notified when an appointment is booked, decoupling side effects from the booking transaction. */
public interface AppointmentObserver {

    void onAppointmentBooked(Appointment appointment);
}
