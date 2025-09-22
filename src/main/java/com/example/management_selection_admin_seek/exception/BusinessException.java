package com.example.management_selection_admin_seek.exception;

/**
 * Base exception for business logic errors
 * Used for domain-specific validation and business rule violations
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
