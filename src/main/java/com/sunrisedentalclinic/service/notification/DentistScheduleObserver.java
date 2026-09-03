package com.sunrisedentalclinic.service.notification;

import com.sunrisedentalclinic.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Stands in for updating the dentist's schedule/dashboard when a new appointment is booked. */
@Component
public class DentistScheduleObserver implements AppointmentObserver {

    private static final Logger log = LoggerFactory.getLogger(DentistScheduleObserver.class);

    @Override
    public void onAppointmentBooked(Appointment appointment) {
        log.info("Schedule updated for Dr. {} at {}",
                appointment.getDentistName(), appointment.getAppointmentAt());
    }
}
