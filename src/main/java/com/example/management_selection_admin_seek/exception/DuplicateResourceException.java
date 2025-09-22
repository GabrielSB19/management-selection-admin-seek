package com.example.management_selection_admin_seek.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 * Maps to HTTP 409 Conflict
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
    }
}
