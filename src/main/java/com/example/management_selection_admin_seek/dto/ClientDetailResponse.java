package com.example.management_selection_admin_seek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for detailed client response with derived calculations
 * Used for GET /clients endpoint to list all clients with additional computed data
 * Includes life expectancy estimation and retirement date calculations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDetailResponse {

    // Basic client information
    private Long id;
    private String name;
    private String lastName;
    private String fullName;
    private Integer age;
    private LocalDate birthDate;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    
    // Derived calculations - future event estimations
    private Integer calculatedCurrentAge;
    private LocalDate estimatedRetirementDate;
    private LocalDate estimatedLifeExpectancy;
    private Integer yearsToRetirement;
    private Integer estimatedRemainingYears;
}
