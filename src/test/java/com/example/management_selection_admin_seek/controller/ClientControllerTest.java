package com.example.management_selection_admin_seek.controller;

import com.example.management_selection_admin_seek.dto.*;
import com.example.management_selection_admin_seek.exception.BusinessException;
import com.example.management_selection_admin_seek.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClientController without Spring Context
 * Tests controller logic and service integration
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientController Unit Tests")
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private ClientCreateRequest validRequest;
    private ClientResponse clientResponse;
    private ClientDetailResponse clientDetailResponse;
    private ClientMetricsResponse metricsResponse;

    @BeforeEach
    void setUp() {
        validRequest = ClientCreateRequest.builder()
                .name("John")
                .lastName("Doe")
                .age(30)
                .birthDate(LocalDate.of(1993, 5, 15))
                .build();

        clientResponse = ClientResponse.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .age(30)
                .birthDate(LocalDate.of(1993, 5, 15))
                .build();

        clientDetailResponse = ClientDetailResponse.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .age(30)
                .birthDate(LocalDate.of(1993, 5, 15))
                .calculatedCurrentAge(32)
                .estimatedRetirementDate(LocalDate.of(2061, 5, 15))
                .build();

        metricsResponse = ClientMetricsResponse.builder()
                .totalClients(5L)
                .averageAge(35.0)
                .standardDeviationAge(8.2)
                .minAge(25)
                .maxAge(45)
                .medianAge(35.0)
                .build();
    }

    @Test
    @DisplayName("Should create client successfully")
    void createClient_ValidRequest_ShouldReturnCreated() {
        // Arrange
        when(clientService.createClient(any(ClientCreateRequest.class))).thenReturn(clientResponse);

        // Act
        ResponseEntity<ClientResponse> response = clientController.createClient(validRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(clientResponse);
        verify(clientService).createClient(validRequest);
    }

    @Test
    @DisplayName("Should handle business exception in create client")
    void createClient_BusinessException_ShouldThrowException() {
        // Arrange
        when(clientService.createClient(any(ClientCreateRequest.class)))
                .thenThrow(new BusinessException("Age validation failed"));

        // Act & Assert
        assertThatThrownBy(() -> clientController.createClient(validRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Age validation failed");

        verify(clientService).createClient(validRequest);
    }

    @Test
    @DisplayName("Should get all clients successfully")
    void getAllClients_ShouldReturnClientList() {
        // Arrange
        List<ClientDetailResponse> clientList = List.of(clientDetailResponse);
        when(clientService.getAllClientsWithDetails()).thenReturn(clientList);

        // Act
        ResponseEntity<List<ClientDetailResponse>> response = clientController.getAllClients();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(clientList);
        verify(clientService).getAllClientsWithDetails();
    }

    @Test
    @DisplayName("Should get empty list when no clients exist")
    void getAllClients_NoClients_ShouldReturnEmptyList() {
        // Arrange
        when(clientService.getAllClientsWithDetails()).thenReturn(List.of());

        // Act
        ResponseEntity<List<ClientDetailResponse>> response = clientController.getAllClients();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(clientService).getAllClientsWithDetails();
    }

    @Test
    @DisplayName("Should get client metrics successfully")
    void getClientMetrics_ShouldReturnMetrics() {
        // Arrange
        when(clientService.getClientMetrics()).thenReturn(metricsResponse);

        // Act
        ResponseEntity<ClientMetricsResponse> response = clientController.getClientMetrics();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(metricsResponse);
        verify(clientService).getClientMetrics();
    }

    @Test
    @DisplayName("Should delegate all operations to service layer")
    void controller_ShouldDelegateToService() {
        // This test verifies that the controller is a thin layer that delegates to services
        
        // Act - Call all controller methods
        when(clientService.createClient(any())).thenReturn(clientResponse);
        when(clientService.getAllClientsWithDetails()).thenReturn(List.of(clientDetailResponse));
        when(clientService.getClientMetrics()).thenReturn(metricsResponse);

        clientController.createClient(validRequest);
        clientController.getAllClients();
        clientController.getClientMetrics();

        // Assert - Verify all service methods were called
        verify(clientService).createClient(any(ClientCreateRequest.class));
        verify(clientService).getAllClientsWithDetails();
        verify(clientService).getClientMetrics();
        verifyNoMoreInteractions(clientService);
    }
}
