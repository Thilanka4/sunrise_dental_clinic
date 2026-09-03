package com.sunrisedentalclinic.exception;

/** Thrown when a dentist already has an appointment at the requested date/time. */
public class DoubleBookingException extends RuntimeException {

    public DoubleBookingException(String message) {
        super(message);
    }
}
