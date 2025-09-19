package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientResponse;
import com.example.management_selection_admin_seek.entity.Client;
import com.example.management_selection_admin_seek.mapper.ClientMapper;
import com.example.management_selection_admin_seek.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * Service for client management
 * Implements business logic for client CRUD operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    /**
     * Create new client
     * REQUIREMENT: "Create new clients through an endpoint that allows 
     * registering name, last name, age and birth date"
     */
    public ClientResponse createClient(ClientCreateRequest request) {
        log.info("Creating new client: {} {}", request.getName(), request.getLastName());
        
        // Validate age consistency with birth date
        validateAgeConsistency(request.getAge(), request.getBirthDate());
        
        // Convert DTO to entity using MapStruct
        Client client = clientMapper.toEntity(request);
        
        // Save to database
        Client savedClient = clientRepository.save(client);
        
        log.info("Client created successfully with ID: {}", savedClient.getId());
        
        // Convert entity to response DTO using MapStruct
        return clientMapper.toResponse(savedClient);
    }

    /**
     * Validate that age is consistent with birth date
     * Allows a maximum difference of 1 year due to birthday timing
     */
    private void validateAgeConsistency(Integer providedAge, LocalDate birthDate) {
        int calculatedAge = Period.between(birthDate, LocalDate.now()).getYears();
        int ageDifference = Math.abs(providedAge - calculatedAge);
        
        if (ageDifference > 1) {
            throw new IllegalArgumentException(
                String.format("Provided age (%d) is not consistent with birth date. " +
                             "Calculated age: %d", providedAge, calculatedAge)
            );
        }
    }
}
