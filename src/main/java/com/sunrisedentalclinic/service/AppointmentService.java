package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dto.AppointmentRegistrationRequest;
import com.sunrisedentalclinic.dto.AppointmentResponse;

public interface AppointmentService {

    AppointmentResponse registerAppointment(AppointmentRegistrationRequest request);

    AppointmentResponse findByAppointmentNumber(String appointmentNumber);
}
