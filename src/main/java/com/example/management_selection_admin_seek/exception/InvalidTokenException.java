package com.example.management_selection_admin_seek.exception;

/**
 * Exception thrown when JWT token is invalid, expired, or malformed
 * Maps to HTTP 401 Unauthorized
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
