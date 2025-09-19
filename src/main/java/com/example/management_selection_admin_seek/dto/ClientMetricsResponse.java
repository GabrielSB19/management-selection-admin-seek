package com.example.management_selection_admin_seek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for client metrics response
 * Contains statistical information about clients
 */
@Schema(description = "Response payload containing statistical metrics and analytics about all clients in the system")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientMetricsResponse {

    @Schema(description = "Total number of clients registered in the system", example = "7", accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalClients;

    @Schema(description = "Average age of all clients", example = "31.5", accessMode = Schema.AccessMode.READ_ONLY)
    private Double averageAge;

    @Schema(description = "Standard deviation of client ages", example = "5.2", accessMode = Schema.AccessMode.READ_ONLY)
    private Double standardDeviationAge;

    @Schema(description = "Minimum age among all clients", example = "25", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer minAge;

    @Schema(description = "Maximum age among all clients", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer maxAge;

    @Schema(description = "Median age of all clients", example = "31.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double medianAge;
}
