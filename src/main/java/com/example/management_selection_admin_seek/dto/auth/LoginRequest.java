package com.example.management_selection_admin_seek.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for user login request
 * Accepts username or email for flexible authentication
 */
@Schema(description = "Request payload for user authentication")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @Schema(
        description = "Username or email for authentication", 
        example = "admin", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 100, message = "Username or email must be between 3 and 100 characters")
    private String identifier;

    @Schema(
        description = "User password", 
        example = "admin123", 
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 8
    )
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Schema(
        description = "Whether to remember the user (longer token expiration)", 
        example = "false",
        defaultValue = "false"
    )
    @Builder.Default
    private Boolean rememberMe = false;
}
