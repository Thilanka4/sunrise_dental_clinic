package com.sunrisedentalclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "treatment_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal treatmentCost;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    protected Bill() {
    }

    public Bill(Appointment appointment, BigDecimal consultationFee, BigDecimal treatmentCost,
                BigDecimal totalCost, LocalDateTime issuedAt) {
        this.appointment = appointment;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalCost = totalCost;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public BigDecimal getTreatmentCost() {
        return treatmentCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}
