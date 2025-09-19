package com.example.management_selection_admin_seek.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO for creating a new client
 * Contains validations according to requirements:
 * - name, last name, age and birth date
 */
@Schema(description = "Request payload for creating a new client in the system")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreateRequest {

    @Schema(
        description = "Client's first name", 
        example = "Juan", 
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 2,
        maxLength = 100
    )
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(
        description = "Client's last name", 
        example = "Pérez", 
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 2,
        maxLength = 100
    )
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @Schema(
        description = "Client's current age in years", 
        example = "30", 
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "18",
        maximum = "120"
    )
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Minimum age is 18 years")
    @Max(value = 120, message = "Maximum age is 120 years")
    private Integer age;

    @Schema(
        description = "Client's birth date (must be in the past and consistent with age)", 
        example = "1993-05-15", 
        requiredMode = Schema.RequiredMode.REQUIRED,
        format = "date"
    )
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
}
