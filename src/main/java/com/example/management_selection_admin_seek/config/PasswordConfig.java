package com.example.management_selection_admin_seek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Configuration - Separate from SecurityConfig
 * 
 * This configuration class provides password-related beans independently
 * to avoid circular dependencies in the Spring application context.
 */
@Configuration
public class PasswordConfig {

    /**
     * Configure password encoder (BCrypt)
     * 
     * Uses BCrypt with strength 12 for production-level security.
     * BCrypt is adaptive and becomes slower over time as computing power increases.
     * 
     * @return BCryptPasswordEncoder instance with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // strength 12 for production security
    }
}
