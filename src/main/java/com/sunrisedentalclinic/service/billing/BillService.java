package com.sunrisedentalclinic.service.billing;

import com.sunrisedentalclinic.dto.BillResponse;

public interface BillService {

    BillResponse generateBill(String appointmentNumber);

    BillResponse findByAppointmentNumber(String appointmentNumber);
}
