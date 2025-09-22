package com.example.management_selection_admin_seek.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * DTO for user registration response
 * Contains confirmation of successful registration and basic user info
 */
@Schema(description = "Response payload after successful user registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    @Schema(description = "Success message", example = "User registered successfully", accessMode = Schema.AccessMode.READ_ONLY)
    private String message;

    @Schema(description = "Newly created user information", accessMode = Schema.AccessMode.READ_ONLY)
    private UserInfo user;

    /**
     * Inner class for user information in registration response
     */
    @Schema(description = "Basic information about the newly registered user")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        
        @Schema(description = "User unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @Schema(description = "Username", example = "johndoe", accessMode = Schema.AccessMode.READ_ONLY)
        private String username;

        @Schema(description = "User email", example = "john.doe@example.com", accessMode = Schema.AccessMode.READ_ONLY)
        private String email;

        @Schema(description = "User full name", example = "John Doe", accessMode = Schema.AccessMode.READ_ONLY)
        private String fullName;

        @Schema(description = "User role (always USER for new registrations)", example = "USER", accessMode = Schema.AccessMode.READ_ONLY)
        private String role;


        @Schema(description = "Account enabled status", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
        private Boolean enabled;
    }
}
