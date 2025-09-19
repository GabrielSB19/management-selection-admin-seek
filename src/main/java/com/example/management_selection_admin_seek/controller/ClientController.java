package com.example.management_selection_admin_seek.controller;

import com.example.management_selection_admin_seek.api.ClientAPI;
import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientResponse;
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
        
        try {
            ClientResponse response = clientService.createClient(request);
            log.info("Client created successfully with ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error creating client: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            
        } catch (Exception e) {
            log.error("Internal error creating client", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
