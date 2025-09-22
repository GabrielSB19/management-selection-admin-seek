package com.example.management_selection_admin_seek.controller;

import com.example.management_selection_admin_seek.api.ClientAPI;
import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientResponse;
import com.example.management_selection_admin_seek.dto.ClientDetailResponse;
import com.example.management_selection_admin_seek.dto.ClientMetricsResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.management_selection_admin_seek.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for client management
 * Basic implementation without OpenAPI documentation
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController implements ClientAPI {

    private final ClientService clientService;

    /**
     * Create new client endpoint
     * POST /api/clients
     */
    @Override
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientCreateRequest request) {
        log.info("POST /api/clients - Creating client: {} {}", request.getName(), request.getLastName());
        
        ClientResponse response = clientService.createClient(request);
        log.info("Client created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all clients endpoint with derived calculations (Paginated)
     * GET /api/client
     */
    @Override
    public ResponseEntity<Page<ClientDetailResponse>> getAllClients(Pageable pageable) {
        log.info("GET /api/client - Getting clients with pagination: page={}, size={}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ClientDetailResponse> clients = clientService.getAllClientsWithDetails(pageable);
        log.info("Retrieved {} clients (page {}/{}) with derived calculations", 
                 clients.getNumberOfElements(), 
                 clients.getNumber() + 1, 
                 clients.getTotalPages());
        return ResponseEntity.ok(clients);
    }

    /**
     * Get client metrics endpoint
     * GET /api/client/metrics
     */
    @Override
    public ResponseEntity<ClientMetricsResponse> getClientMetrics() {
        log.info("GET /api/client/metrics - Getting client metrics");
        
        ClientMetricsResponse metrics = clientService.getClientMetrics();
        log.info("Metrics retrieved successfully - Total clients: {}", metrics.getTotalClients());
        return ResponseEntity.ok(metrics);
    }
}
