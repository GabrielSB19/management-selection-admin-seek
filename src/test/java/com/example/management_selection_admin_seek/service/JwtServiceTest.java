package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import com.example.management_selection_admin_seek.mapper.TokenMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtService
 * Tests JWT token generation, validation, and claim extraction
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private JwtService jwtService;

    private User testUser;
    private String testSecret;
    private SecretKey testSecretKey;

    @BeforeEach
    void setUp() {
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

        // Set up test JWT secret (Base64 encoded)
        testSecret = "TXlTZWNyZXRLZXlGb3JUZXN0aW5nUHVycG9zZXNPbmx5MTIzNDU2Nzg5MDEyMzQ1Njc4OTA=";
        testSecretKey = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(testSecret));
        
        // Inject test values using reflection
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 86400000L); // 24 hours
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_ValidUser_ShouldReturnValidToken() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");

        // Act
        String token = jwtService.generateToken(testUser);

        // Assert
        assertThat(token).isNotNull().isNotEmpty();
        
        // Verify token structure (should have 3 parts separated by dots)
        assertThat(token.split("\\.")).hasSize(3);
        
        // Verify token is valid
        assertThat(jwtService.isTokenValid(token, testUser)).isTrue();
        
        verify(tokenMapper).mapAuthorities(testUser);
        verify(tokenMapper).mapUserRole(testUser);
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void generateRefreshToken_ValidUser_ShouldReturnValidToken() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");

        // Act
        String refreshToken = jwtService.generateRefreshToken(testUser);

        // Assert
        assertThat(refreshToken).isNotNull().isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3);
        assertThat(jwtService.isTokenValid(refreshToken, testUser)).isTrue();
    }

    @Test
    @DisplayName("Should extract username from token correctly")
    void extractUsername_ValidToken_ShouldReturnUsername() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");
        
        String token = jwtService.generateToken(testUser);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should extract expiration from token correctly")
    void extractExpiration_ValidToken_ShouldReturnFutureDate() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");
        
        String token = jwtService.generateToken(testUser);

        // Act
        Date extractedExpiration = jwtService.extractExpiration(token);

        // Assert
        assertThat(extractedExpiration).isAfter(new Date());
        assertThat(extractedExpiration).isBefore(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)));
    }

    @Test
    @DisplayName("Should validate token with correct user")
    void isTokenValid_ValidTokenAndUser_ShouldReturnTrue() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");
        
        String token = jwtService.generateToken(testUser);

        // Act
        boolean isValid = jwtService.isTokenValid(token, testUser);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token with different user")
    void isTokenValid_TokenForDifferentUser_ShouldReturnFalse() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");
        
        String token = jwtService.generateToken(testUser);
        
        User differentUser = User.builder()
                .username("differentuser")
                .email("different@example.com")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should detect expired token")
    void isTokenExpired_ExpiredToken_ShouldReturnTrue() {
        // Arrange - Create a token that's already expired
        Date expiredDate = Date.from(Instant.now().minus(1, ChronoUnit.HOURS));
        String expiredToken = Jwts.builder()
                .subject(testUser.getUsername())
                .issuedAt(new Date())
                .expiration(expiredDate)
                .signWith(testSecretKey)
                .compact();

        // Act
        boolean isExpired = jwtService.isTokenExpired(expiredToken);

        // Assert
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Should detect non-expired token")
    void isTokenExpired_ValidToken_ShouldReturnFalse() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");
        
        String token = jwtService.generateToken(testUser);

        // Act
        boolean isExpired = jwtService.isTokenExpired(token);

        // Assert
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Should extract custom claims correctly")
    void extractClaim_CustomClaims_ShouldReturnCorrectValues() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");
        
        String token = jwtService.generateToken(testUser);

        // Act
        @SuppressWarnings("unchecked")
        List<String> authorities = jwtService.extractClaim(token, claims -> 
                (List<String>) claims.get("authorities", List.class));
        String userRole = jwtService.extractClaim(token, claims -> 
                claims.get("userRole", String.class));
        Boolean enabled = jwtService.extractClaim(token, claims -> 
                claims.get("enabled", Boolean.class));

        // Assert
        assertThat(authorities).containsExactly("ROLE_USER");
        assertThat(userRole).isEqualTo("USER");
        assertThat(enabled).isTrue();
    }

    @Test
    @DisplayName("Should include all required claims in token")
    void generateToken_ShouldIncludeAllRequiredClaims() {
        // Arrange
        when(tokenMapper.mapAuthorities(testUser))
                .thenReturn(List.of("ROLE_USER"));
        when(tokenMapper.mapUserRole(testUser))
                .thenReturn("USER");

        // Act
        String token = jwtService.generateToken(testUser);

        // Assert - Extract and verify all claims
        Claims claims = Jwts.parser()
                .verifyWith(testSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("userRole", String.class)).isEqualTo("USER");
        assertThat(claims.get("fullName", String.class)).isEqualTo("Test User");
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities", List.class);
        assertThat(authorities).containsExactly("ROLE_USER");
        assertThat(claims.get("enabled", Boolean.class)).isTrue();
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    @DisplayName("Should return correct JWT expiration in seconds")
    void getJwtExpirationInSeconds_ShouldReturnCorrectValue() {
        // Act
        long expirationInSeconds = jwtService.getJwtExpirationInSeconds();

        // Assert
        assertThat(expirationInSeconds).isEqualTo(3600L); // 1 hour = 3600 seconds
    }

    @Test
    @DisplayName("Should handle malformed token gracefully")
    void extractUsername_MalformedToken_ShouldThrowException() {
        // Arrange
        String malformedToken = "this.is.not.a.valid.jwt.token";

        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle null or empty token gracefully")
    void extractUsername_NullToken_ShouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(null))
                .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> jwtService.extractUsername(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
