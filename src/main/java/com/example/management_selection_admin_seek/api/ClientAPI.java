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

import java.util.List;

import jakarta.validation.Valid;

/**
 * This interface defines the API for the Client entity
 */
@RequestMapping(ClientAPI.BASE_URL)
public interface ClientAPI {

    String BASE_URL = "/client";


    /**
     * Create a new client
     * @param request the client to create
     * @return the created client
     */
    @PostMapping
    ResponseEntity<ClientResponse> createClient(@RequestBody @Valid ClientCreateRequest request);

    /**
     * Get all clients with complete data and derived calculations
     * @return list of all clients with life expectancy and retirement estimations
     */
    @GetMapping
    ResponseEntity<List<ClientDetailResponse>> getAllClients();

    /**
     * Get client metrics
     * @return statistical metrics about existing clients
     */
    @GetMapping("/metrics")
    ResponseEntity<ClientMetricsResponse> getClientMetrics();
}