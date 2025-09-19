package com.example.management_selection_admin_seek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for client response
 * Includes complete client information
 */
@Schema(description = "Response payload containing basic client information after creation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    @Schema(description = "Unique client identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Client's first name", example = "Juan")
    private String name;

    @Schema(description = "Client's last name", example = "Pérez")
    private String lastName;

    @Schema(description = "Client's full name (automatically generated)", example = "Juan Pérez", accessMode = Schema.AccessMode.READ_ONLY)
    private String fullName;

    @Schema(description = "Client's current age in years", example = "30")
    private Integer age;

    @Schema(description = "Client's birth date", example = "1993-05-15", format = "date")
    private LocalDate birthDate;

    @Schema(description = "Timestamp when the client was created", example = "2025-09-19T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime creationDate;

    @Schema(description = "Timestamp when the client was last updated", example = "2025-09-19T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateDate;
}
