package com.sunrisedentalclinic.service.notification;

import com.sunrisedentalclinic.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Stands in for sending the patient a booking confirmation (SMS/email). */
@Component
public class PatientConfirmationObserver implements AppointmentObserver {

    private static final Logger log = LoggerFactory.getLogger(PatientConfirmationObserver.class);

    @Override
    public void onAppointmentBooked(Appointment appointment) {
        log.info("Confirmation sent to {} for appointment {}",
                appointment.getPatient().getFullName(), appointment.getAppointmentNumber());
    }
}
