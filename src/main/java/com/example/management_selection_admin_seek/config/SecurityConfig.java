package com.example.management_selection_admin_seek.config;

import com.example.management_selection_admin_seek.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration with JWT Authentication
 * 
 * This configuration class sets up JWT-based authentication for the application:
 * - Configures stateless JWT authentication (no sessions)
 * - Defines authorization rules for different endpoints
 * - Sets up password encoding with BCrypt
 * - Integrates JWT authentication filter
 * - Configures authentication provider with custom UserDetailsService
 * 
 * Security Rules:
 * - /api/auth/** → Public (login, register, refresh)
 * - /api/swagger-ui/** → Public (API documentation)
 * - /api/actuator/** → Public (monitoring endpoints)  
 * - /api/admin/** → ADMIN role required
 * - /api/client/** → Any authenticated user
 * - All other endpoints → Authentication required
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Configure security filter chain with JWT authentication
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Client endpoints require authentication (any role)
                .requestMatchers("/client/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            .authenticationProvider(authenticationProvider())
            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure authentication provider with custom UserDetailsService
     * Suppressing deprecation warnings as we use the stable approach until Spring Security upgrade
     */
    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(false); // For better error handling
        return authProvider;
    }

    // UserDetailsService bean not needed as we inject UserService directly

    /**
     * Configure AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
