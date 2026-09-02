package com.sunrisedentalclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", nullable = false, unique = true, length = 30)
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @Column(name = "dentist_name", nullable = false, length = 150)
    private String dentistName;

    @Column(name = "appointment_at", nullable = false)
    private LocalDateTime appointmentAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    protected Appointment() {
    }

    public Appointment(String appointmentNumber, Patient patient, Treatment treatment,
                       String dentistName, LocalDateTime appointmentAt) {
        this.appointmentNumber = appointmentNumber;
        this.patient = patient;
        this.treatment = treatment;
        this.dentistName = dentistName;
        this.appointmentAt = appointmentAt;
    }

    public Long getId() {
        return id;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public String getDentistName() {
        return dentistName;
    }

    public LocalDateTime getAppointmentAt() {
        return appointmentAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }
}
