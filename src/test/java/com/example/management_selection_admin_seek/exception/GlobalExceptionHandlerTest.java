package com.example.management_selection_admin_seek.exception;

import com.example.management_selection_admin_seek.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler
 * Tests exception handling and HTTP status code mapping
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("Should handle validation errors with 422 status")
    void handleValidationErrors_ShouldReturn422WithFieldErrors() {
        // Arrange
        FieldError fieldError1 = new FieldError("object", "field1", "rejected1", false, null, null, "Field1 is required");
        FieldError fieldError2 = new FieldError("object", "field2", "rejected2", false, null, null, "Field2 must be valid");
        
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        
        // Create a MethodParameter using a simple approach (this was the issue!)
        java.lang.reflect.Method method;
        try {
            method = Object.class.getMethod("toString"); // Using a simple public method
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        org.springframework.core.MethodParameter methodParameter = new org.springframework.core.MethodParameter(method, -1);
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationErrors(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(422);
            assertThat(errorResponse.getError()).isEqualTo("VALIDATION_ERROR");
            assertThat(errorResponse.getMessage()).isEqualTo("Invalid input data provided");
            assertThat(errorResponse.getPath()).isEqualTo("/api/test");
            assertThat(errorResponse.getFieldErrors()).hasSize(2);
            
            ErrorResponse.FieldError firstError = errorResponse.getFieldErrors().get(0);
            assertThat(firstError.getField()).isEqualTo("field1");
            assertThat(firstError.getRejectedValue()).isEqualTo("rejected1");
            assertThat(firstError.getMessage()).isEqualTo("Field1 is required");
        });
    }

    @Test
    @DisplayName("Should handle constraint violations with 422 status")
    void handleConstraintViolation_ShouldReturn422() {
        // Arrange
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(exception.getMessage()).thenReturn("Constraint violation message");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(422);
            assertThat(errorResponse.getError()).isEqualTo("CONSTRAINT_VIOLATION");
            assertThat(errorResponse.getMessage()).isEqualTo("Constraint validation failed");
            assertThat(errorResponse.getDetails()).isEqualTo("Constraint violation message");
        });
    }

    @Test
    @DisplayName("Should handle business exceptions with 400 status")
    void handleBusinessException_ShouldReturn400() {
        // Arrange
        BusinessException exception = new BusinessException("Business rule violated");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(400);
            assertThat(errorResponse.getError()).isEqualTo("BUSINESS_RULE_VIOLATION");
            assertThat(errorResponse.getMessage()).isEqualTo("Business rule violated");
            assertThat(errorResponse.getDetails()).isEqualTo("The request violates a business rule");
        });
    }

    @Test
    @DisplayName("Should handle resource not found with 404 status")
    void handleResourceNotFound_ShouldReturn404() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceNotFound(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(404);
            assertThat(errorResponse.getError()).isEqualTo("RESOURCE_NOT_FOUND");
            assertThat(errorResponse.getMessage()).isEqualTo("Resource not found");
        });
    }

    @Test
    @DisplayName("Should handle duplicate resource with 409 status")
    void handleDuplicateResource_ShouldReturn409() {
        // Arrange
        DuplicateResourceException exception = new DuplicateResourceException("Resource already exists");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDuplicateResource(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(409);
            assertThat(errorResponse.getError()).isEqualTo("RESOURCE_CONFLICT");
            assertThat(errorResponse.getMessage()).isEqualTo("Resource already exists");
        });
    }

    @Test
    @DisplayName("Should handle authentication exception with 401 status")
    void handleAuthenticationException_ShouldReturn401() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthenticationException(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(401);
            assertThat(errorResponse.getError()).isEqualTo("AUTHENTICATION_FAILED");
            assertThat(errorResponse.getMessage()).isEqualTo("Authentication failed");
        });
    }

    @Test
    @DisplayName("Should handle custom authentication exception with 401 status")
    void handleCustomAuthenticationException_ShouldReturn401() {
        // Arrange
        com.example.management_selection_admin_seek.exception.AuthenticationException exception = new com.example.management_selection_admin_seek.exception.AuthenticationException("Custom auth error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCustomAuthenticationException(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(401);
            assertThat(errorResponse.getError()).isEqualTo("AUTHENTICATION_ERROR");
            assertThat(errorResponse.getMessage()).isEqualTo("Custom auth error");
        });
    }

    @Test
    @DisplayName("Should handle invalid token exception with 401 status")
    void handleInvalidToken_ShouldReturn401() {
        // Arrange
        InvalidTokenException exception = new InvalidTokenException("Token is invalid");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidToken(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(401);
            assertThat(errorResponse.getError()).isEqualTo("INVALID_TOKEN");
            assertThat(errorResponse.getMessage()).isEqualTo("Invalid or expired token");
        });
    }

    @Test
    @DisplayName("Should handle access denied with 403 status")
    void handleAccessDenied_ShouldReturn403() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDenied(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(403);
            assertThat(errorResponse.getError()).isEqualTo("ACCESS_DENIED");
            assertThat(errorResponse.getMessage()).isEqualTo("Access denied");
        });
    }

    @Test
    @DisplayName("Should handle type mismatch with 400 status")
    void handleTypeMismatch_ShouldReturn400() {
        // Arrange
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getValue()).thenReturn("invalid_value");
        when(exception.getName()).thenReturn("parameter_name");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatch(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(400);
            assertThat(errorResponse.getError()).isEqualTo("INVALID_PARAMETER_TYPE");
            assertThat(errorResponse.getMessage()).contains("invalid_value");
            assertThat(errorResponse.getMessage()).contains("parameter_name");
        });
    }

    @Test
    @DisplayName("Should handle data integrity violation with 409 status")
    void handleDataIntegrityViolation_ShouldReturn409() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data integrity violation");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(409);
            assertThat(errorResponse.getError()).isEqualTo("DATA_INTEGRITY_VIOLATION");
            assertThat(errorResponse.getMessage()).isEqualTo("Data integrity constraint violation");
        });
    }

    @Test
    @DisplayName("Should handle illegal argument exception with 400 status")
    void handleIllegalArgument_ShouldReturn400() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(400);
            assertThat(errorResponse.getError()).isEqualTo("INVALID_ARGUMENT");
            assertThat(errorResponse.getMessage()).isEqualTo("Invalid argument provided");
        });
    }

    @Test
    @DisplayName("Should handle generic exception with 500 status")
    void handleGenericException_ShouldReturn500() {
        // Arrange
        RuntimeException exception = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getStatus()).isEqualTo(500);
            assertThat(errorResponse.getError()).isEqualTo("INTERNAL_SERVER_ERROR");
            assertThat(errorResponse.getMessage()).isEqualTo("An unexpected error occurred");
            assertThat(errorResponse.getDetails()).isEqualTo("Please contact support if the problem persists");
        });
    }

    @Test
    @DisplayName("Should include timestamp in all error responses")
    void allHandlers_ShouldIncludeTimestamp() {
        // Arrange
        BusinessException exception = new BusinessException("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception, request);

        // Assert
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getTimestamp()).isNotNull();
        });
    }

    @Test
    @DisplayName("Should include correct path in all error responses")
    void allHandlers_ShouldIncludeCorrectPath() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/custom/path");
        BusinessException exception = new BusinessException("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception, request);

        // Assert
        assertThat(response.getBody()).isNotNull().satisfies(errorResponse -> {
            assertThat(errorResponse.getPath()).isEqualTo("/api/custom/path");
        });
    }
}
