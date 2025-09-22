package com.example.management_selection_admin_seek.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for JWT token refresh request
 * Used to obtain a new access token using a valid refresh token
 */
@Schema(description = "Request payload for refreshing JWT access token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @Schema(
        description = "Valid refresh token to exchange for new access token", 
        example = "eyJhbGciOiJIUzI1NiJ9...", 
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
