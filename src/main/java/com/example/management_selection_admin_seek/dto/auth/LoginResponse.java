package com.example.management_selection_admin_seek.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * DTO for user login response
 * Contains JWT tokens and user information
 */
@Schema(description = "Response payload after successful authentication")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    @Schema(description = "JWT access token for API authentication", example = "eyJhbGciOiJIUzI1NiJ9...", accessMode = Schema.AccessMode.READ_ONLY)
    private String accessToken;

    @Schema(description = "JWT refresh token for obtaining new access tokens", example = "eyJhbGciOiJIUzI1NiJ9...", accessMode = Schema.AccessMode.READ_ONLY)
    private String refreshToken;

    @Schema(description = "Token type (always Bearer)", example = "Bearer", accessMode = Schema.AccessMode.READ_ONLY)
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiration time in seconds", example = "86400", accessMode = Schema.AccessMode.READ_ONLY)
    private Long expiresIn;

    @Schema(description = "User information", accessMode = Schema.AccessMode.READ_ONLY)
    private UserInfo user;

    /**
     * Inner class for user information in login response
     */
    @Schema(description = "Basic user information included in login response")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        
        @Schema(description = "User unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @Schema(description = "Username", example = "admin", accessMode = Schema.AccessMode.READ_ONLY)
        private String username;

        @Schema(description = "User email", example = "admin@seek.com", accessMode = Schema.AccessMode.READ_ONLY)
        private String email;

        @Schema(description = "User full name", example = "System Administrator", accessMode = Schema.AccessMode.READ_ONLY)
        private String fullName;

        @Schema(description = "User role", example = "ADMIN", accessMode = Schema.AccessMode.READ_ONLY)
        private String role;

    }
}
