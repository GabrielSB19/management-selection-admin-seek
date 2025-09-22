package com.example.management_selection_admin_seek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for detailed client response with derived calculations
 * Used for GET /clients endpoint to list all clients with additional computed data
 * Includes life expectancy estimation and retirement date calculations
 */
@Schema(description = "Response payload containing complete client information with derived calculations and future event estimations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDetailResponse {

    // Basic client information
    @Schema(description = "Unique client identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Client's first name", example = "Juan")
    private String name;

    @Schema(description = "Client's last name", example = "Pérez")
    private String lastName;

    @Schema(description = "Client's full name (automatically generated)", example = "Juan Pérez", accessMode = Schema.AccessMode.READ_ONLY)
    private String fullName;

    @Schema(description = "Client's reported age in years", example = "30")
    private Integer age;

    @Schema(description = "Client's birth date", example = "1993-05-15", format = "date")
    private LocalDate birthDate;

    @Schema(description = "Timestamp when the client was created", example = "2025-09-19T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime creationDate;

    @Schema(description = "Timestamp when the client was last updated", example = "2025-09-19T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateDate;
    
    // Derived calculations - future event estimations
    @Schema(description = "Current age calculated from birth date", example = "32", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer calculatedCurrentAge;

    @Schema(description = "Estimated retirement date (at 65 years old)", example = "2058-09-19", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate estimatedRetirementDate;

    @Schema(description = "Estimated life expectancy date (78 years average)", example = "2071-09-19", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate estimatedLifeExpectancy;

    @Schema(description = "Years remaining until retirement", example = "33", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer yearsToRetirement;

    @Schema(description = "Estimated remaining years of life", example = "46", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer estimatedRemainingYears;
}
