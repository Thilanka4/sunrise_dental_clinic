package com.sunrisedentalclinic.exception;

/** Thrown when an operation is not valid for an appointment's current status. */
public class InvalidAppointmentStateException extends RuntimeException {

    public InvalidAppointmentStateException(String message) {
        super(message);
    }
}
