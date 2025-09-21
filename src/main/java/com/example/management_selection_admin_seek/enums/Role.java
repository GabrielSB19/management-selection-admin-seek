package com.example.management_selection_admin_seek.enums;

/**
 * User roles enumeration for the application.
 * Defines different user privilege levels.
 */
public enum Role {
    /**
     * Administrator role with full system access
     */
    ADMIN("ROLE_ADMIN"),
    
    /**
     * Regular user role with limited access
     */
    USER("ROLE_USER");

    private final String authority;

    /**
     * Constructor for Role enum
     * @param authority the Spring Security authority string
     */
    Role(String authority) {
        this.authority = authority;
    }

    /**
     * Gets the Spring Security authority string for this role
     * @return the authority string (e.g., "ROLE_ADMIN", "ROLE_USER")
     */
    public String getAuthority() {
        return authority;
    }
}
