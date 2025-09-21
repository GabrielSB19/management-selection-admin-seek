package com.example.management_selection_admin_seek.exception;

/**
 * Exception thrown for authentication-related errors
 * Maps to HTTP 401 Unauthorized
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
