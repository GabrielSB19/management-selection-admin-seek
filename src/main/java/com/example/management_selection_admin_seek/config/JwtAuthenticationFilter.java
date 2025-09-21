package com.example.management_selection_admin_seek.config;

import com.example.management_selection_admin_seek.service.JwtService;
import com.example.management_selection_admin_seek.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - Core Security Component
 * 
 * This filter is the heart of JWT authentication in the application:
 * 
 * RESPONSIBILITIES:
 * - Intercepts ALL HTTP requests before they reach controllers
 * - Extracts JWT tokens from Authorization header (Bearer format)
 * - Validates token integrity, expiration, and user existence
 * - Sets Spring Security authentication context for authorized requests
 * - Handles JWT errors gracefully without breaking the request flow
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Extract JWT token from request
            String jwt = extractTokenFromRequest(request);
            
            if (jwt == null) {
                // No token found, continue with filter chain
                filterChain.doFilter(request, response);
                return;
            }

            // Extract username from JWT token
            String username = jwtService.extractUsername(jwt);
            
            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                // Username is null or user is already authenticated
                filterChain.doFilter(request, response);
                return;
            }

            // Load user details
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            // Validate JWT token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                
                // Set authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                log.debug("JWT authentication successful for user: {}", username);
            } else {
                log.debug("JWT token validation failed for user: {}", username);
            }

        } catch (JwtException e) {
            log.warn("JWT token processing failed: {}", e.getMessage());
            // Clear security context in case of JWT errors
            SecurityContextHolder.clearContext();
            
        } catch (Exception e) {
            log.error("Error processing JWT authentication", e);
            // Clear security context in case of unexpected errors
            SecurityContextHolder.clearContext();
        }

        // Continue with filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from HTTP request Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * Determine if this filter should be applied to the request
     * Skip authentication for public endpoints
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT authentication for public endpoints
        // Spring Security sees paths without context path, so we match accordingly
        return path.startsWith("/auth/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/actuator/") ||
               path.equals("/error");
    }
}
