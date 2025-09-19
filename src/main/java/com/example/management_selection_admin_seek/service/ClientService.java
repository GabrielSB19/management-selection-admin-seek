package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientResponse;
import com.example.management_selection_admin_seek.dto.ClientDetailResponse;
import com.example.management_selection_admin_seek.dto.ClientMetricsResponse;
import com.example.management_selection_admin_seek.entity.Client;
import com.example.management_selection_admin_seek.mapper.ClientMapper;
import com.example.management_selection_admin_seek.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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
    private final ClientCalculationService calculationService;

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

    /**
     * Get all clients with complete data and derived calculations
     * REQUIREMENT: "List all registered clients with their complete data 
     * and a derived calculation, such as an estimated date for a future event"
     */
    @Transactional(readOnly = true)
    public List<ClientDetailResponse> getAllClientsWithDetails() {
        log.info("Getting all clients with derived calculations");
        
        List<Client> clients = clientRepository.findAll();
        
        List<ClientDetailResponse> clientResponses = clients.stream()
                .map(this::buildClientDetailResponse)
                .toList();
        
        log.info("Retrieved {} clients with derived calculations", clientResponses.size());
        
        return clientResponses;
    }

    /**
     * Build ClientDetailResponse with derived calculations
     * Uses mapper for basic transformation and calculation service for business logic
     */
    private ClientDetailResponse buildClientDetailResponse(Client client) {
        // Get basic mapping first (without calculations)
        ClientDetailResponse response = clientMapper.toDetailResponse(client);
        
        // Add derived calculations using calculation service
        LocalDate birthDate = client.getBirthDate();
        response.setCalculatedCurrentAge(calculationService.calculateCurrentAge(birthDate));
        response.setEstimatedRetirementDate(calculationService.calculateRetirementDate(birthDate));
        response.setEstimatedLifeExpectancy(calculationService.calculateLifeExpectancy(birthDate));
        response.setYearsToRetirement(calculationService.calculateYearsToRetirement(birthDate));
        response.setEstimatedRemainingYears(calculationService.calculateRemainingYears(birthDate));
        
        return response;
    }

    /**
     * Get client metrics
     * REQUIREMENT: "Query a set of metrics about existing clients, 
     * such as average age and standard deviation of ages"
     */
    @Transactional(readOnly = true)
    public ClientMetricsResponse getClientMetrics() {
        log.info("Calculating client metrics");
        
        // Get basic counts and stats from database
        long totalClients = clientRepository.count();
        
        if (totalClients == 0) {
            log.info("No clients found, returning empty metrics");
            return ClientMetricsResponse.builder()
                .totalClients(0L)
                .averageAge(0.0)
                .standardDeviationAge(0.0)
                .minAge(0)
                .maxAge(0)
                .medianAge(0.0)
                .build();
        }
        
        // Get statistical data
        Double averageAge = clientRepository.findAverageAge();
        Integer minAge = clientRepository.findMinAge();
        Integer maxAge = clientRepository.findMaxAge();
        List<Integer> allAges = clientRepository.findAllAges();
        
        // Calculate standard deviation
        double standardDeviation = calculateStandardDeviation(allAges, averageAge);
        
        // Calculate median
        double median = calculateMedian(allAges);
        
        log.info("Metrics calculated - Total: {}, Avg: {}, StdDev: {}", 
                totalClients, averageAge, standardDeviation);
        
        return ClientMetricsResponse.builder()
            .totalClients(totalClients)
            .averageAge(Math.round(averageAge * 100.0) / 100.0) // Round to 2 decimals
            .standardDeviationAge(Math.round(standardDeviation * 100.0) / 100.0)
            .minAge(minAge)
            .maxAge(maxAge)
            .medianAge(Math.round(median * 100.0) / 100.0)
            .build();
    }

    /**
     * Calculate standard deviation of ages
     */
    private double calculateStandardDeviation(List<Integer> ages, double mean) {
        if (ages.size() <= 1) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (Integer age : ages) {
            sum += Math.pow(age - mean, 2);
        }
        
        return Math.sqrt(sum / ages.size());
    }

    /**
     * Calculate median of ages
     */
    private double calculateMedian(List<Integer> sortedAges) {
        int size = sortedAges.size();
        if (size == 0) {
            return 0.0;
        }
        
        if (size % 2 == 0) {
            // Even number of elements - average of two middle values
            return (sortedAges.get(size / 2 - 1) + sortedAges.get(size / 2)) / 2.0;
        } else {
            // Odd number of elements - middle value
            return sortedAges.get(size / 2);
        }
    }
}
