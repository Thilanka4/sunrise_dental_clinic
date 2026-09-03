package com.sunrisedentalclinic.exception;

/** Thrown when a requested appointment, treatment, or other entity does not exist. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
