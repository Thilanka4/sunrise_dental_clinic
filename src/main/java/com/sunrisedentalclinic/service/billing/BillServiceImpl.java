package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.config.ClinicSettings;
import com.sunrisedentalclinic.dto.BillResponse;
import com.sunrisedentalclinic.exception.DuplicateBillException;
import com.sunrisedentalclinic.exception.InvalidAppointmentStateException;
import com.sunrisedentalclinic.exception.ResourceNotFoundException;
import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.AppointmentStatus;
import com.sunrisedentalclinic.model.Bill;
import com.sunrisedentalclinic.repository.AppointmentRepository;
import com.sunrisedentalclinic.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BillServiceImpl implements BillService {

    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final DiscountStrategyFactory discountStrategyFactory;

    public BillServiceImpl(AppointmentRepository appointmentRepository,
            BillRepository billRepository,
            DiscountStrategyFactory discountStrategyFactory) {
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
        this.discountStrategyFactory = discountStrategyFactory;
    }

    @Override
    @Transactional
    public BillResponse generateBill(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No appointment found with number " + appointmentNumber));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidAppointmentStateException(
                    "Cannot generate a bill for cancelled appointment " + appointmentNumber);
        }
        if (billRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new DuplicateBillException("A bill already exists for appointment " + appointmentNumber);
        }

        DiscountStrategy discountStrategy = discountStrategyFactory.resolve(
                appointment.getPatient(), appointment.getTreatment());

        Bill bill = new BillBuilder(appointment)
                .consultationFee(ClinicSettings.getInstance().getStandardConsultationFee())
                .treatmentCost(appointment.getTreatment().getBaseCost())
                .discountStrategy(discountStrategy)
                .build();
        bill = billRepository.save(bill);

        appointment.markCompleted();
        appointmentRepository.save(appointment);

        return toResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse findByAppointmentNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No appointment found with number " + appointmentNumber));
        Bill bill = billRepository.findByAppointmentId(appointment.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No bill found for appointment " + appointmentNumber));
        return toResponse(bill);
    }

    private BillResponse toResponse(Bill bill) {
        Appointment appointment = bill.getAppointment();
        BigDecimal discountAmount = bill.getConsultationFee()
                .add(bill.getTreatmentCost())
                .subtract(bill.getTotalCost());
        return new BillResponse(
                appointment.getAppointmentNumber(),
                appointment.getPatient().getFullName(),
                appointment.getTreatment().getName(),
                bill.getConsultationFee(),
                bill.getTreatmentCost(),
                discountAmount,
                bill.getTotalCost(),
                bill.getIssuedAt());
    }
}
