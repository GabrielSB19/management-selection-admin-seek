package com.example.management_selection_admin_seek.config;

import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import com.example.management_selection_admin_seek.service.JwtService;
import com.example.management_selection_admin_seek.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for JwtAuthenticationFilter
 * Validates JWT authentication flow and security logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Tests")
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Clear security context before each test
        SecurityContextHolder.clearContext();

        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        validToken = "valid.jwt.token";
    }

    @Test
    @DisplayName("Should skip filter for public auth endpoints")
    void shouldNotFilter_AuthEndpoint_ShouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/auth/login");

        // Act
        boolean shouldSkip = jwtAuthenticationFilter.shouldNotFilter(request);

        // Assert
        assertThat(shouldSkip).isTrue();
    }

    @Test
    @DisplayName("Should skip filter for swagger endpoints")
    void shouldNotFilter_SwaggerEndpoint_ShouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        // Act
        boolean shouldSkip = jwtAuthenticationFilter.shouldNotFilter(request);

        // Assert
        assertThat(shouldSkip).isTrue();
    }

    @Test
    @DisplayName("Should skip filter for actuator endpoints")
    void shouldNotFilter_ActuatorEndpoint_ShouldReturnTrue() throws ServletException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // Act
        boolean shouldSkip = jwtAuthenticationFilter.shouldNotFilter(request);

        // Assert
        assertThat(shouldSkip).isTrue();
    }

    @Test
    @DisplayName("Should not skip filter for protected endpoints")
    void shouldNotFilter_ProtectedEndpoint_ShouldReturnFalse() throws ServletException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/client");

        // Act
        boolean shouldSkip = jwtAuthenticationFilter.shouldNotFilter(request);

        // Assert
        assertThat(shouldSkip).isFalse();
    }

    @Test
    @DisplayName("Should continue filter chain when no authorization header present")
    void doFilterInternal_NoAuthHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should continue filter chain when authorization header doesn't start with Bearer")
    void doFilterInternal_InvalidAuthHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should authenticate user when valid JWT token provided")
    void doFilterInternal_ValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtService.isTokenValid(validToken, testUser)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verify(userService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid(validToken, testUser);

        // Verify authentication is set
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(testUser);
        assertThat(auth.getAuthorities()).isEqualTo(testUser.getAuthorities());
    }

    @Test
    @DisplayName("Should not authenticate when JWT token is invalid")
    void doFilterInternal_InvalidToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtService.isTokenValid(validToken, testUser)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verify(userService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid(validToken, testUser);

        // Verify authentication is NOT set
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
    }

    @Test
    @DisplayName("Should continue filter chain when username cannot be extracted from token")
    void doFilterInternal_NoUsername_ShouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verifyNoInteractions(userService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should skip authentication when user is already authenticated")
    void doFilterInternal_AlreadyAuthenticated_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("testuser");
        
        // Set existing authentication
        Authentication existingAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verifyNoInteractions(userService);
        
        // Verify existing authentication is preserved
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
    }

    @Test
    @DisplayName("Should handle JWT exceptions gracefully")
    void doFilterInternal_JwtException_ShouldClearContextAndContinue() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenThrow(new JwtException("Invalid JWT"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verifyNoInteractions(userService);
        
        // Verify security context is cleared
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should handle general exceptions gracefully")
    void doFilterInternal_GeneralException_ShouldClearContextAndContinue() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(validToken);
        verifyNoInteractions(userService);
        
        // Verify security context is cleared
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should extract token correctly from valid Bearer authorization header")
    void extractTokenFromRequest_ValidBearerToken_ShouldReturnToken() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // Act - Use reflection to test private method (or test through doFilterInternal)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert - Verify token was processed (jwtService.extractUsername was called)
        verify(jwtService).extractUsername(validToken);
    }

    @Test
    @DisplayName("Should handle empty authorization header")
    void extractTokenFromRequest_EmptyHeader_ShouldReturnNull() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should handle authorization header with only Bearer prefix")
    void extractTokenFromRequest_OnlyBearerPrefix_ShouldReturnEmptyString() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(""); // Empty string after "Bearer "
    }

    @Test
    @DisplayName("Should not interfere with filter chain execution")
    void doFilterInternal_ShouldAlwaysCallFilterChain() throws ServletException, IOException {
        // Arrange - Different scenarios
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtService.isTokenValid(validToken, testUser)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert - Filter chain should ALWAYS be called
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should work with different valid Bearer token formats")
    void doFilterInternal_DifferentTokenFormats_ShouldWork() throws ServletException, IOException {
        // Test with a more realistic JWT-like token
        String realisticToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature";
        
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + realisticToken);
        when(jwtService.extractUsername(realisticToken)).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(jwtService.isTokenValid(realisticToken, testUser)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService).extractUsername(realisticToken);
        verify(userService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid(realisticToken, testUser);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(testUser);
    }
}
