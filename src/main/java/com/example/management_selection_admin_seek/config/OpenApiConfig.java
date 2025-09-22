package com.example.management_selection_admin_seek.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 Configuration with JWT Authentication
 * 
 * This configuration sets up Swagger UI with comprehensive API documentation:
 * 
 * FEATURES:
 * - JWT Bearer token authentication scheme
 * - Professional API information and contact details  
 * - Server configuration for different environments
 * - Security documentation for protected endpoints
 * 
 * AUTHENTICATION:
 * - Users can authenticate directly in Swagger UI
 * - JWT tokens are automatically included in protected endpoint requests
 * - Clear indication of which endpoints require authentication (🔒)
 * 
 * USAGE:
 * 1. Navigate to /api/swagger-ui/index.html
 * 2. Use "Authorize" button to enter JWT token
 * 3. Format: Bearer {your-jwt-token}
 * 4. Protected endpoints will show lock icon and include authentication
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Management Selection Admin API",
        version = "1.0.0",
        description = """
            Candidate Management System API for Backend Java Developer Technical Challenge
            
            This RESTful API provides comprehensive client management capabilities including:
            • Client registration with validation and business rules
            • Client analytics and statistical metrics  
            • Advanced client queries with derived calculations
            • JWT-based authentication and authorization
            • Role-based access control (ADMIN/USER roles)
            
            **Authentication Required:**
            Most endpoints require JWT authentication. Use the /auth/login endpoint to obtain tokens,
            then click the 'Authorize' button above to authenticate your requests.
            
            **Technical Stack:**
            • Spring Boot 3.5.6 with Spring Security
            • MySQL database with Flyway migrations
            • JWT stateless authentication
            • MapStruct for DTO mapping
            • Comprehensive validation and error handling
            """,
        contact = @Contact(
            name = "API Support",
            email = "support@seek.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Local Development Server",
            url = "http://localhost:8080/api"
        ),
        @Server(
            description = "Production Server", 
            url = "https://api.seek.com/api"
        )
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    description = """
        JWT Bearer token authentication
        
        **How to authenticate:**
        1. Use POST /auth/login with valid credentials to get JWT token
        2. Click 'Authorize' button above
        3. Enter: Bearer {your-jwt-access-token}
        4. Click 'Authorize' to apply to all protected endpoints
        
        **Token Format:** Bearer eyJhbGciOiJIUzI1NiJ9...
        
        **Token Expiration:** Access tokens expire in 24 hours
        Use POST /auth/refresh to get new tokens with your refresh token
        """,
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    
}
