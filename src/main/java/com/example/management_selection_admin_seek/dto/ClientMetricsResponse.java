package com.example.management_selection_admin_seek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for client metrics response
 * Contains statistical information about clients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientMetricsResponse {

    private Long totalClients;
    private Double averageAge;
    private Double standardDeviationAge;
    private Integer minAge;
    private Integer maxAge;
    private Double medianAge;
}
