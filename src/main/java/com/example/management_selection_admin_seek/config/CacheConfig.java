package com.example.management_selection_admin_seek.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Cache Configuration
 * Enables Spring Cache abstraction for the application
 * 
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * Cache configuration is handled via application.properties:
     * - spring.cache.type=simple
     */
}
