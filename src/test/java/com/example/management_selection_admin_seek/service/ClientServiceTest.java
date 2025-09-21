package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientDetailResponse;
import com.example.management_selection_admin_seek.dto.ClientMetricsResponse;
import com.example.management_selection_admin_seek.dto.ClientResponse;
import com.example.management_selection_admin_seek.entity.Client;
import com.example.management_selection_admin_seek.exception.BusinessException;
import com.example.management_selection_admin_seek.mapper.ClientMapper;
import com.example.management_selection_admin_seek.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClientService
 * Tests client creation, validation, metrics calculation, and derived data
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private ClientCalculationService calculationService;

    @InjectMocks
    private ClientService clientService;

    private ClientCreateRequest validRequest;
    private Client validClient;
    private ClientResponse expectedResponse;

    @BeforeEach
    void setUp() {
        LocalDate birthDate = LocalDate.of(1993, 5, 15);
        
        validRequest = ClientCreateRequest.builder()
                .name("John")
                .lastName("Doe")
                .age(31) // Consistent with birth date (2025 - 1993 = 32, but within 1 year tolerance)
                .birthDate(birthDate)
                .build();

        validClient = Client.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .age(31)
                .birthDate(birthDate)
                .build();

        expectedResponse = ClientResponse.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .age(31)
                .birthDate(birthDate)
                .build();
    }

    @Test
    @DisplayName("Should create client successfully with valid data")
    void createClient_ValidData_ShouldReturnClientResponse() {
        // Arrange
        when(clientMapper.toEntity(validRequest)).thenReturn(validClient);
        when(clientRepository.save(validClient)).thenReturn(validClient);
        when(clientMapper.toResponse(validClient)).thenReturn(expectedResponse);

        // Act
        ClientResponse result = clientService.createClient(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getAge()).isEqualTo(31);

        verify(clientMapper).toEntity(validRequest);
        verify(clientRepository).save(validClient);
        verify(clientMapper).toResponse(validClient);
    }

    @Test
    @DisplayName("Should throw BusinessException when age is inconsistent with birth date")
    void createClient_InconsistentAge_ShouldThrowBusinessException() {
        // Arrange - age 25 is inconsistent with birth date 1993 (should be ~32)
        ClientCreateRequest inconsistentRequest = ClientCreateRequest.builder()
                .name("John")
                .lastName("Doe")
                .age(25) // More than 1 year difference
                .birthDate(LocalDate.of(1993, 5, 15))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> clientService.createClient(inconsistentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Provided age (25) is not consistent with birth date");

        // Verify that save was never called
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should accept age within 1 year tolerance of calculated age")
    void createClient_AgeWithinTolerance_ShouldSucceed() {
        // Arrange - current year 2025, birth year 1993, calculated age ~32
        // Testing ages 31, 32, 33 (all within 1 year tolerance)
        List<Integer> validAges = Arrays.asList(31, 32, 33);
        
        for (Integer age : validAges) {
            ClientCreateRequest request = ClientCreateRequest.builder()
                    .name("John")
                    .lastName("Doe")
                    .age(age)
                    .birthDate(LocalDate.of(1993, 5, 15))
                    .build();

            when(clientMapper.toEntity(any())).thenReturn(validClient);
            when(clientRepository.save(any())).thenReturn(validClient);
            when(clientMapper.toResponse(any())).thenReturn(expectedResponse);

            // Act & Assert - should not throw exception
            assertThatCode(() -> clientService.createClient(request))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Should get all clients with derived calculations")
    void getAllClientsWithDetails_ShouldReturnClientsWithCalculations() {
        // Arrange
        List<Client> clients = Arrays.asList(validClient);
        ClientDetailResponse detailResponse = ClientDetailResponse.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .age(31)
                .birthDate(LocalDate.of(1993, 5, 15))
                .build();

        when(clientRepository.findAll()).thenReturn(clients);
        when(clientMapper.toDetailResponse(validClient)).thenReturn(detailResponse);
        when(calculationService.calculateCurrentAge(any())).thenReturn(32);
        when(calculationService.calculateRetirementDate(any())).thenReturn(LocalDate.of(2058, 5, 15));
        when(calculationService.calculateLifeExpectancy(any())).thenReturn(LocalDate.of(2073, 5, 15));

        // Act
        List<ClientDetailResponse> result = clientService.getAllClientsWithDetails();

        // Assert
        assertThat(result).hasSize(1);
        ClientDetailResponse response = result.get(0);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John");

        verify(clientRepository).findAll();
        verify(clientMapper).toDetailResponse(validClient);
        verify(calculationService).calculateCurrentAge(LocalDate.of(1993, 5, 15));
        verify(calculationService).calculateRetirementDate(LocalDate.of(1993, 5, 15));
        verify(calculationService).calculateLifeExpectancy(LocalDate.of(1993, 5, 15));
    }

    @Test
    @DisplayName("Should calculate client metrics correctly")
    void getClientMetrics_ShouldReturnCorrectMetrics() {
        // Arrange
        List<Integer> ages = Arrays.asList(25, 30, 35, 40, 45);
        when(clientRepository.findAverageAge()).thenReturn(35.0);
        when(clientRepository.findAllAges()).thenReturn(ages);
        when(clientRepository.findMinAge()).thenReturn(25);
        when(clientRepository.findMaxAge()).thenReturn(45);
        when(clientRepository.count()).thenReturn(5L);

        // Act
        ClientMetricsResponse result = clientService.getClientMetrics();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalClients()).isEqualTo(5L);
        assertThat(result.getAverageAge()).isEqualTo(35.0);
        assertThat(result.getMinAge()).isEqualTo(25);
        assertThat(result.getMaxAge()).isEqualTo(45);
        assertThat(result.getMedianAge()).isEqualTo(35.0); // Middle value of 5 elements
        assertThat(result.getStandardDeviationAge()).isPositive();

        verify(clientRepository).findAverageAge();
        verify(clientRepository).findAllAges();
        verify(clientRepository).findMinAge();
        verify(clientRepository).findMaxAge();
        verify(clientRepository).count();
    }

    @Test
    @DisplayName("Should calculate standard deviation correctly")
    void getClientMetrics_ShouldCalculateStandardDeviationCorrectly() {
        // Arrange - Using known values for easy verification
        List<Integer> ages = Arrays.asList(20, 30, 40); // Mean = 30, SD = 10
        when(clientRepository.findAverageAge()).thenReturn(30.0);
        when(clientRepository.findAllAges()).thenReturn(ages);
        when(clientRepository.findMinAge()).thenReturn(20);
        when(clientRepository.findMaxAge()).thenReturn(40);
        when(clientRepository.count()).thenReturn(3L);

        // Act
        ClientMetricsResponse result = clientService.getClientMetrics();

        // Assert
        // Standard deviation should be approximately 8.16 (sqrt of 66.67)
        assertThat(result.getStandardDeviationAge()).isCloseTo(8.16, within(0.1));
    }

    @Test
    @DisplayName("Should calculate median correctly for odd number of clients")
    void getClientMetrics_OddNumberOfClients_ShouldCalculateMedianCorrectly() {
        // Arrange
        List<Integer> ages = Arrays.asList(20, 25, 30, 35, 40); // Median should be 30
        when(clientRepository.findAverageAge()).thenReturn(30.0);
        when(clientRepository.findAllAges()).thenReturn(ages);
        when(clientRepository.findMinAge()).thenReturn(20);
        when(clientRepository.findMaxAge()).thenReturn(40);
        when(clientRepository.count()).thenReturn(5L);

        // Act
        ClientMetricsResponse result = clientService.getClientMetrics();

        // Assert
        assertThat(result.getMedianAge()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should calculate median correctly for even number of clients")
    void getClientMetrics_EvenNumberOfClients_ShouldCalculateMedianCorrectly() {
        // Arrange
        List<Integer> ages = Arrays.asList(20, 25, 35, 40); // Median should be (25+35)/2 = 30
        when(clientRepository.findAverageAge()).thenReturn(30.0);
        when(clientRepository.findAllAges()).thenReturn(ages);
        when(clientRepository.findMinAge()).thenReturn(20);
        when(clientRepository.findMaxAge()).thenReturn(40);
        when(clientRepository.count()).thenReturn(4L);

        // Act
        ClientMetricsResponse result = clientService.getClientMetrics();

        // Assert
        assertThat(result.getMedianAge()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle empty client list gracefully")
    void getClientMetrics_EmptyClientList_ShouldReturnZeroMetrics() {
        // Arrange
        when(clientRepository.count()).thenReturn(0L);

        // Act
        ClientMetricsResponse result = clientService.getClientMetrics();

        // Assert
        assertThat(result.getTotalClients()).isEqualTo(0L);
        assertThat(result.getAverageAge()).isEqualTo(0.0);
        assertThat(result.getMinAge()).isNull();
        assertThat(result.getMaxAge()).isNull();
        assertThat(result.getMedianAge()).isEqualTo(0.0);
        assertThat(result.getStandardDeviationAge()).isEqualTo(0.0);
    }
}
