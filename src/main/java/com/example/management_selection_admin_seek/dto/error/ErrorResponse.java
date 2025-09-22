package com.example.management_selection_admin_seek.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response DTO for API error handling
 * Provides consistent error information across all endpoints
 */
@Schema(description = "Standardized error response for API exceptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type identifier", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "Human-readable error message", example = "Invalid input data provided")
    private String message;

    @Schema(description = "Detailed error description", example = "The request contains invalid field values")
    private String details;

    @Schema(description = "API endpoint where the error occurred", example = "/api/client")
    private String path;

    @Schema(description = "Timestamp when the error occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "List of field validation errors")
    private List<FieldError> fieldErrors;

    /**
     * Inner class for field-specific validation errors
     */
    @Schema(description = "Field validation error details")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        
        @Schema(description = "Name of the field with error", example = "email")
        private String field;
        
        @Schema(description = "Rejected value", example = "invalid-email")
        private Object rejectedValue;
        
        @Schema(description = "Error message for this field", example = "Email must be valid")
        private String message;
    }
}
