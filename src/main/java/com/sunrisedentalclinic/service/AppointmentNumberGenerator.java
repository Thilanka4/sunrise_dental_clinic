package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.repository.AppointmentRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates human-readable, sequential appointment numbers (e.g. APT000042).
 *
 * <p>The counter is seeded from the current row count at startup and held in memory,
 * which is sufficient for a single-instance deployment as used in this project. The
 * {@code appointment_number} column's UNIQUE constraint remains the hard backstop
 * against collisions.
 */
@Component
public class AppointmentNumberGenerator {

    private static final String PREFIX = "APT";

    private final AtomicLong counter;

    public AppointmentNumberGenerator(AppointmentRepository appointmentRepository) {
        this.counter = new AtomicLong(appointmentRepository.count());
    }

    public String next() {
        return PREFIX + String.format("%06d", counter.incrementAndGet());
    }
}
