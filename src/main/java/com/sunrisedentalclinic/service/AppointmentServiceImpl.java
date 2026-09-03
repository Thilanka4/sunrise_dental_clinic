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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;
    private final AppointmentNumberGenerator appointmentNumberGenerator;
    private final AppointmentBookingNotifier appointmentBookingNotifier;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            TreatmentRepository treatmentRepository,
            AppointmentNumberGenerator appointmentNumberGenerator,
            AppointmentBookingNotifier appointmentBookingNotifier) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.treatmentRepository = treatmentRepository;
        this.appointmentNumberGenerator = appointmentNumberGenerator;
        this.appointmentBookingNotifier = appointmentBookingNotifier;
    }

    @Override
    @Transactional
    public AppointmentResponse registerAppointment(AppointmentRegistrationRequest request) {
        Treatment treatment = treatmentRepository.findById(request.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No treatment found with id " + request.getTreatmentId()));

        boolean dentistBusy = appointmentRepository.existsByDentistNameIgnoreCaseAndAppointmentAtAndStatusNot(
                request.getDentistName(), request.getAppointmentAt(), AppointmentStatus.CANCELLED);
        if (dentistBusy) {
            throw new DoubleBookingException(
                    "Dr. " + request.getDentistName() + " already has an appointment at " + request.getAppointmentAt());
        }

        Patient patient = patientRepository.save(
                new Patient(request.getPatientName(), request.getAddress(), request.getContactNumber()));

        Appointment appointment = new Appointment(
                appointmentNumberGenerator.next(), patient, treatment, request.getDentistName(), request.getAppointmentAt());
        appointment = appointmentRepository.save(appointment);
        appointmentBookingNotifier.notifyObservers(appointment);

        return toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse findByAppointmentNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No appointment found with number " + appointmentNumber));
        return toResponse(appointment);
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getAppointmentNumber(),
                appointment.getPatient().getFullName(),
                appointment.getPatient().getAddress(),
                appointment.getPatient().getContactNumber(),
                appointment.getDentistName(),
                appointment.getTreatment().getName(),
                appointment.getTreatment().getBaseCost(),
                appointment.getAppointmentAt(),
                appointment.getStatus());
    }
}
