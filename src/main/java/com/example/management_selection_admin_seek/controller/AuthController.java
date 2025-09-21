package com.example.management_selection_admin_seek.controller;

import com.example.management_selection_admin_seek.api.AuthAPI;
import com.example.management_selection_admin_seek.dto.auth.*;
import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.mapper.AuthMapper;
import com.example.management_selection_admin_seek.service.JwtService;
import com.example.management_selection_admin_seek.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication REST Controller - JWT Security Endpoints
 * 
 * This controller provides all authentication-related endpoints for the application:
 * 
 * PUBLIC ENDPOINTS (no authentication required):
 * - POST /api/auth/login: User authentication with credentials
 * - POST /api/auth/register: New user account creation  
 * - POST /api/auth/refresh: JWT token renewal
 * 
 * ERROR HANDLING:
 * - 200 OK: Successful operations (login, refresh)
 * - 201 CREATED: Successful registration
 * - 401 UNAUTHORIZED: Invalid credentials or expired refresh token
 * - 409 CONFLICT: Username or email already exists during registration
 * - 422 UNPROCESSABLE ENTITY: Validation errors in request data
 * - 500 INTERNAL SERVER ERROR: Unexpected server errors
 * 
 * All endpoints include comprehensive OpenAPI documentation for API consumers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthAPI {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    /**
     * User Login Endpoint - JWT Authentication
     * 
     * Authenticates user credentials and returns JWT tokens for API access.
     * 
     * 
     * @param request LoginRequest with username/email and password
     * @return LoginResponse with JWT tokens and user info, or 401 if invalid
     * 
     * POST /api/auth/login
     */
    @Override
    public ResponseEntity<LoginResponse> login(@Valid LoginRequest request) {
        log.info("Login attempt for: {}", request.getIdentifier());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getIdentifier(),
                    request.getPassword()
                )
            );

            // Get authenticated user
            User user = (User) authentication.getPrincipal();
            
            // Generate JWT tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // Build response using mapper (same pattern as other controllers)
            LoginResponse response = authMapper.toLoginResponse(
                    user, 
                    accessToken, 
                    refreshToken, 
                    jwtService.getJwtExpirationInSeconds()
            );

            log.info("Login successful for user: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.warn("Login failed for: {}", request.getIdentifier());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * User Registration Endpoint - Account Creation
     * 
     * Creates new user accounts with validation and secure password storage.
     * 
     * @param request RegisterRequest with user details and password confirmation
     * @return RegisterResponse with success message and user info, or error status
     * 
     * POST /api/auth/register
     */
    @Override
    public ResponseEntity<RegisterResponse> register(@Valid RegisterRequest request) {
        log.info("Registration attempt for: {}", request.getUsername());

        try {
            RegisterResponse response = userService.registerUser(request);
            log.info("Registration successful for: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed for {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
            
        } catch (Exception e) {
            log.error("Registration error for: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * JWT Token Refresh Endpoint - Token Renewal
     * 
     * Renews JWT access tokens using valid refresh tokens for extended API access.
     * 
     * 
     * TOKEN ROTATION:
     * - Old refresh token becomes invalid after successful refresh
     * - New tokens have fresh expiration times from current moment
     * - Prevents long-lived token vulnerabilities
     * 
     * @param request RefreshTokenRequest with current refresh token
     * @return LoginResponse with new JWT tokens, or 401 if refresh token invalid
     * 
     * POST /api/auth/refresh
     */
    @Override
    public ResponseEntity<LoginResponse> refreshToken(@Valid RefreshTokenRequest request) {
        log.debug("Token refresh attempt");

        try {
            String refreshToken = request.getRefreshToken();
            
            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            
            // Load user details
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            // Validate refresh token
            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Generate new tokens
            String newAccessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            // Build response using mapper (same pattern as other controllers)
            LoginResponse response = authMapper.toRefreshResponse(
                    newAccessToken, 
                    newRefreshToken, 
                    jwtService.getJwtExpirationInSeconds()
            );

            log.debug("Token refresh successful for: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.warn("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
