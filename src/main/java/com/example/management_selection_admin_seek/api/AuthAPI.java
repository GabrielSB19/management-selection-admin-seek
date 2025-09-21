package com.example.management_selection_admin_seek.api;

import com.example.management_selection_admin_seek.dto.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Authentication API Interface
 * Defines endpoints for user authentication and registration
 */
@Tag(name = "Authentication", description = "API for user authentication, registration, and token management")
@RequestMapping(AuthAPI.BASE_URL)
public interface AuthAPI {

    String BASE_URL = "/auth";

    /**
     * User login endpoint
     */
    @Operation(
        summary = "User login",
        description = "Authenticate user with username/email and password. Returns JWT tokens for API access. " +
                      "Supports both username and email as identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Successful Login",
                    value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9...\",\"refreshToken\":\"eyJhbGciOiJIUzI1NiJ9...\",\"tokenType\":\"Bearer\",\"expiresIn\":86400,\"user\":{\"id\":1,\"username\":\"admin\",\"email\":\"admin@seek.com\",\"fullName\":\"System Administrator\",\"role\":\"ADMIN\"}}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid username or password\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "423", 
            description = "Account locked due to too many failed attempts",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Account Locked",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":423,\"error\":\"Locked\",\"message\":\"Account locked due to too many failed login attempts\"}"
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
        @Parameter(description = "User login credentials", required = true)
        @RequestBody @Valid LoginRequest request
    );

    /**
     * User registration endpoint
     */
    @Operation(
        summary = "User registration",
        description = "Register a new user account. Username and email must be unique. " +
                      "Password must contain at least one letter and one number."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterResponse.class),
                examples = @ExampleObject(
                    name = "Successful Registration",
                    value = "{\"message\":\"User registered successfully\",\"user\":{\"id\":2,\"username\":\"johndoe\",\"email\":\"john.doe@example.com\",\"fullName\":\"John Doe\",\"role\":\"USER\",\"enabled\":true}}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Username or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Username Exists",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":409,\"error\":\"Conflict\",\"message\":\"Username already exists: johndoe\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Validation error - Invalid data provided",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":422,\"error\":\"Unprocessable Entity\",\"message\":\"Password must contain at least one letter and one number\"}"
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(
        @Parameter(description = "User registration data", required = true)
        @RequestBody @Valid RegisterRequest request
    );

    /**
     * Refresh JWT token endpoint
     */
    @Operation(
        summary = "Refresh JWT token",
        description = "Obtain a new access token using a valid refresh token. " +
                      "Refresh tokens have longer expiration time than access tokens."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Token refreshed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Token Refreshed",
                    value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9...\",\"refreshToken\":\"eyJhbGciOiJIUzI1NiJ9...\",\"tokenType\":\"Bearer\",\"expiresIn\":86400}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid or expired refresh token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Refresh Token",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid refresh token\"}"
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh")
    ResponseEntity<LoginResponse> refreshToken(
        @Parameter(description = "Refresh token request", required = true)
        @RequestBody @Valid RefreshTokenRequest request
    );

}
