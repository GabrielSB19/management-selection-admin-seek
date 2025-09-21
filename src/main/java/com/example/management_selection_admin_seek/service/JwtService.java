package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.mapper.TokenMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service - Token Management and Security Operations
 * 
 * This service handles all JWT-related operations for secure authentication:
 * 
 * CORE FUNCTIONS:
 * - Token Generation: Creates access and refresh tokens with user claims
 * - Token Validation: Verifies token signature, expiration, and user match
 * - Claim Extraction: Parses specific claims from JWT tokens (username, expiration, etc.)
 * - Security Key Management: Handles signing key generation and validation
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final TokenMapper tokenMapper;

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${app.jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * Extract username from JWT token
     * 
     * Parses the JWT token and extracts the 'subject' claim which contains the username.
     * Used by JwtAuthenticationFilter during request authentication.
     * 
     * @param token JWT token string
     * @return username from token subject claim, or null if invalid token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generate JWT access token for authenticated user
     * 
     * Creates a short-lived access token (24h) containing user claims and authorities.
     * Used after successful login or during token refresh operations.
     * 
     * TOKEN INCLUDES:
     * - Subject: username
     * - Authorities: user roles and permissions
     * - User ID: for quick user identification
     * - User role: primary role (ADMIN/USER)
     * - Full name: for UI display
     * - Enabled status: account status
     * - Standard claims: issued at, expiration
     * 
     * @param userDetails authenticated user details with authorities
     * @return JWT access token string for API authentication
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate JWT access token with extra claims
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Generate JWT refresh token for user
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Build JWT token with specified expiration
     * Uses TokenMapper for simple claim extraction
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        // Cast to User entity (same pattern as other services)
        User user = (User) userDetails;
        
        // Use mapper for claim extraction (simple and clean)
        List<String> authorities = tokenMapper.mapAuthorities(userDetails);
        String userRole = tokenMapper.mapUserRole(user);

        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(expiration);

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .claim("authorities", authorities)
                .claim("enabled", user.isEnabled())
                .claim("userId", user.getId())
                .claim("userRole", userRole)
                .claim("fullName", user.getFullName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validate JWT token against user details
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if JWT token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT token expiration check failed: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is null or empty: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get signing key for JWT operations
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Get JWT expiration time in seconds
     */
    public Long getJwtExpirationInSeconds() {
        return jwtExpiration / 1000;
    }
}
