package com.sunrisedentalclinic.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Uniform error body for the REST API so clients get a consistent shape for
 * both validation failures (fieldErrors populated) and business errors.
 */
public record ApiError(Instant timestamp, int status, String error, String message, Map<String, String> fieldErrors) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, Map.of());
    }

    public static ApiError of(int status, String error, String message, Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, fieldErrors);
    }
}
