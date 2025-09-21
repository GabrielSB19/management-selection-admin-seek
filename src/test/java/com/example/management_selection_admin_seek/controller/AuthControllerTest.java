package com.example.management_selection_admin_seek.controller;

import com.example.management_selection_admin_seek.dto.auth.*;
import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import com.example.management_selection_admin_seek.exception.DuplicateResourceException;
import com.example.management_selection_admin_seek.exception.InvalidTokenException;
import com.example.management_selection_admin_seek.mapper.AuthMapper;
import com.example.management_selection_admin_seek.service.JwtService;
import com.example.management_selection_admin_seek.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthController without Spring Context
 * Tests controller logic and service integration
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private RegisterResponse registerResponse;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private RefreshTokenRequest refreshRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        registerResponse = RegisterResponse.builder()
                .message("User registered successfully")
                .user(RegisterResponse.UserInfo.builder()
                        .id(1L)
                        .username("testuser")
                        .email("test@example.com")
                        .fullName("Test User")
                        .role("USER")
                        .enabled(true)
                        .build())
                .build();

        loginRequest = LoginRequest.builder()
                .identifier("testuser")
                .password("password123")
                .rememberMe(false)
                .build();

        loginResponse = LoginResponse.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(LoginResponse.UserInfo.builder()
                        .id(1L)
                        .username("testuser")
                        .email("test@example.com")
                        .fullName("Test User")
                        .role("USER")
                        .build())
                .build();

        refreshRequest = RefreshTokenRequest.builder()
                .refreshToken("valid_refresh_token")
                .build();
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_ValidRequest_ShouldReturnCreated() {
        // Arrange
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(registerResponse);

        // Act
        ResponseEntity<RegisterResponse> response = authController.register(registerRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(registerResponse);
        verify(userService).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should handle duplicate user registration")
    void register_DuplicateUser_ShouldThrowException() {
        // Arrange
        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("Username already exists"));

        // Act & Assert
        assertThatThrownBy(() -> authController.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");

        verify(userService).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void login_ValidCredentials_ShouldReturnOk() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh_token");
        when(jwtService.getJwtExpirationInSeconds()).thenReturn(3600L);
        when(authMapper.toLoginResponse(testUser, "access_token", "refresh_token", 3600L))
                .thenReturn(loginResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(loginResponse);
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(testUser);
        verify(jwtService).generateRefreshToken(testUser);
        verify(authMapper).toLoginResponse(testUser, "access_token", "refresh_token", 3600L);
    }

    @Test
    @DisplayName("Should handle invalid credentials")
    void login_InvalidCredentials_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
        verify(authMapper, never()).toLoginResponse(any(User.class), anyString(), anyString(), any(Long.class));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void refreshToken_ValidToken_ShouldReturnOk() {
        // Arrange
        when(jwtService.extractUsername("valid_refresh_token")).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtService.isTokenValid("valid_refresh_token", testUser)).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("new_refresh_token");
        when(jwtService.getJwtExpirationInSeconds()).thenReturn(3600L);
        when(authMapper.toRefreshResponse("new_access_token", "new_refresh_token", 3600L))
                .thenReturn(loginResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.refreshToken(refreshRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(loginResponse);
        
        verify(jwtService).extractUsername("valid_refresh_token");
        verify(userService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid("valid_refresh_token", testUser);
        verify(jwtService).generateToken(testUser);
        verify(jwtService).generateRefreshToken(testUser);
        verify(authMapper).toRefreshResponse("new_access_token", "new_refresh_token", 3600L);
    }

    @Test
    @DisplayName("Should handle invalid refresh token")
    void refreshToken_InvalidToken_ShouldThrowException() {
        // Arrange
        when(jwtService.extractUsername("invalid_token")).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtService.isTokenValid("invalid_token", testUser)).thenReturn(false);

        // Act & Assert
        RefreshTokenRequest invalidRequest = RefreshTokenRequest.builder()
                .refreshToken("invalid_token")
                .build();

        assertThatThrownBy(() -> authController.refreshToken(invalidRequest))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Refresh token is invalid or expired");

        verify(jwtService).extractUsername("invalid_token");
        verify(userService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid("invalid_token", testUser);
        verify(jwtService, never()).generateToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
        verify(authMapper, never()).toRefreshResponse(anyString(), anyString(), any(Long.class));
    }

    @Test
    @DisplayName("Should delegate all operations to service layer")
    void controller_ShouldDelegateToServices() {
        // This test verifies that the controller is a thin layer that delegates to services
        
        // Arrange
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(registerResponse);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");
        when(jwtService.getJwtExpirationInSeconds()).thenReturn(3600L);
        when(authMapper.toLoginResponse(any(User.class), anyString(), anyString(), any(Long.class))).thenReturn(loginResponse);

        // Act
        authController.register(registerRequest);
        authController.login(loginRequest);

        // Assert
        verify(userService).registerUser(any(RegisterRequest.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
        verify(authMapper).toLoginResponse(any(User.class), anyString(), anyString(), any(Long.class));
    }
}
