package com.sunrisedentalclinic.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Input for registering a new appointment. A mutable JavaBean (not a record) so
 * Thymeleaf's th:field data binding and Jackson's JSON deserialization can both
 * populate it directly.
 */
public class AppointmentRegistrationRequest {

    @NotBlank(message = "Patient name is required")
    @Size(max = 150, message = "Patient name must be at most 150 characters")
    private String patientName;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9+()\\-\\s]{7,30}$", message = "Enter a valid contact number")
    private String contactNumber;

    @NotBlank(message = "Dentist name is required")
    @Size(max = 150, message = "Dentist name must be at most 150 characters")
    private String dentistName;

    @NotNull(message = "Please select a treatment")
    private Long treatmentId;

    @NotNull(message = "Appointment date and time is required")
    @Future(message = "Appointment date and time must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentAt;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }

    public LocalDateTime getAppointmentAt() {
        return appointmentAt;
    }

    public void setAppointmentAt(LocalDateTime appointmentAt) {
        this.appointmentAt = appointmentAt;
    }
}
