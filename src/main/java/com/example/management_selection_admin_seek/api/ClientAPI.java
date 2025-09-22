package com.example.management_selection_admin_seek.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientResponse;
import com.example.management_selection_admin_seek.dto.ClientDetailResponse;
import com.example.management_selection_admin_seek.dto.ClientMetricsResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Client Management API - Protected Endpoints
 * Provides endpoints for client registration, listing, and analytics
 * 
 * AUTHENTICATION REQUIRED: All endpoints require valid JWT authentication
 * Use the 'Authorize' button above to authenticate with your access token
 */
@Tag(name = "Client Management", description = "API for managing clients in the candidate selection system (🔒 Authentication Required)")
@RequestMapping(ClientAPI.BASE_URL)
public interface ClientAPI {

    String BASE_URL = "/client";

    /**
     * Create a new client
     */
    @Operation(
        summary = "🔒 Create a new client",
        description = "Register a new client with personal information including name, age, and birth date. " +
                      "Age consistency with birth date is validated (max 1 year difference allowed). " +
                      "**Requires JWT authentication.**",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Client created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClientResponse.class),
                examples = @ExampleObject(
                    name = "Client Created",
                    value = "{\"id\":1,\"name\":\"Juan\",\"lastName\":\"Pérez\",\"fullName\":\"Juan Pérez\",\"age\":32,\"birthDate\":\"1993-05-15\",\"creationDate\":\"2025-09-19T10:00:00\",\"updateDate\":\"2025-09-19T10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"JWT token is missing or invalid\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Validation error - Invalid data provided",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":422,\"error\":\"Unprocessable Entity\",\"message\":\"Age inconsistency detected\"}"
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    ResponseEntity<ClientResponse> createClient(
        @Parameter(description = "Client information to create", required = true)
        @RequestBody @Valid ClientCreateRequest request
    );

    /**
     * Get all clients with derived calculations (Paginated)
     */
    @Operation(
        summary = "🔒 Get all clients with derived calculations (Paginated)",
        description = "Retrieve clients with their basic information plus derived calculations including " +
                      "life expectancy, retirement date, and remaining years. Supports pagination for optimal " +
                      "performance with large datasets. **Requires JWT authentication.**",
        security = @SecurityRequirement(name = "Bearer Authentication"),
        parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Page size (max 100)", example = "20"),
        }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Clients retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Paginated Clients Response",
                    value = """
                        {
                          "content": [{
                            "id": 1,
                            "firstName": "Juan",
                            "lastName": "Pérez",
                            "fullName": "Juan Pérez",
                            "age": 32,
                            "birthDate": "1993-05-15",
                            "calculatedCurrentAge": 32,
                            "estimatedRetirementDate": "2058-09-19",
                            "estimatedLifeExpectancy": "2071-09-19",
                            "yearsToRetirement": 33,
                            "estimatedRemainingYears": 46
                          }],
                          "pageable": {
                            "sort": {"sorted": true, "orderBy": "firstName,asc"},
                            "pageNumber": 0,
                            "pageSize": 20
                          },
                          "totalElements": 50,
                          "totalPages": 3,
                          "last": false,
                          "first": true,
                          "numberOfElements": 20
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"JWT token is missing or invalid\"}"
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    ResponseEntity<Page<ClientDetailResponse>> getAllClients(
        @Parameter(hidden = true) Pageable pageable
    );

    /**
     * Get client metrics and statistics
     */
    @Operation(
        summary = "🔒 Get client analytics and metrics",
        description = "Retrieve statistical information about all clients including average age, " +
                      "standard deviation, minimum and maximum ages, median age, and total client count. " +
                      "**Requires JWT authentication.**",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Metrics retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClientMetricsResponse.class),
                examples = @ExampleObject(
                    name = "Client Metrics",
                    value = "{\"totalClients\":7,\"averageAge\":31.5,\"standardDeviationAge\":5.2,\"minAge\":25,\"maxAge\":42,\"medianAge\":31.0}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = "{\"timestamp\":\"2025-09-19T10:00:00.000+00:00\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"JWT token is missing or invalid\"}"
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/metrics")
    ResponseEntity<ClientMetricsResponse> getClientMetrics();
}