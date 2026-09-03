package com.sunrisedentalclinic.exception;

/** Thrown when a bill is requested for an appointment that already has one. */
public class DuplicateBillException extends RuntimeException {

    public DuplicateBillException(String message) {
        super(message);
    }
}
