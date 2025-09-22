package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientDetailResponse;
import com.example.management_selection_admin_seek.dto.ClientResponse;
import com.example.management_selection_admin_seek.entity.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ClientMapper
 * Validates MapStruct generated mapping methods
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Client Mapper Tests")
class ClientMapperTest {

    @Autowired
    private ClientMapper clientMapper;

    @Test
    @DisplayName("Should convert ClientCreateRequest to Client entity")
    void toEntity_ShouldMapAllFieldsCorrectly() {
        // Arrange
        ClientCreateRequest request = ClientCreateRequest.builder()
                .name("John")
                .lastName("Doe")
                .age(30)
                .birthDate(LocalDate.of(1993, 5, 15))
                .build();

        // Act
        Client result = clientMapper.toEntity(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getAge()).isEqualTo(30);
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1993, 5, 15));
        
        // Fields that should be null (not mapped from request)
        assertThat(result.getId()).isNull();
        assertThat(result.getCreationDate()).isNull();
        assertThat(result.getUpdateDate()).isNull();
    }

    @Test
    @DisplayName("Should convert Client entity to ClientResponse")
    void toResponse_ShouldMapAllFieldsCorrectly() {
        // Arrange
        Client client = Client.builder()
                .id(1L)
                .name("Jane")
                .lastName("Smith")
                .age(25)
                .birthDate(LocalDate.of(1998, 8, 20))
                .creationDate(LocalDateTime.of(2023, 1, 1, 10, 0))
                .updateDate(LocalDateTime.of(2023, 1, 1, 10, 0))
                .build();

        // Act
        ClientResponse result = clientMapper.toResponse(client);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAge()).isEqualTo(25);
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1998, 8, 20));
        assertThat(result.getFullName()).isEqualTo("Jane Smith");
        assertThat(result.getCreationDate()).isEqualTo(LocalDateTime.of(2023, 1, 1, 10, 0));
        assertThat(result.getUpdateDate()).isEqualTo(LocalDateTime.of(2023, 1, 1, 10, 0));
    }

    @Test
    @DisplayName("Should convert Client entity to ClientDetailResponse with derived fields ignored")
    void toDetailResponse_ShouldMapBasicFieldsAndIgnoreDerivedOnes() {
        // Arrange
        Client client = Client.builder()
                .id(2L)
                .name("Bob")
                .lastName("Johnson")
                .age(40)
                .birthDate(LocalDate.of(1983, 12, 10))
                .creationDate(LocalDateTime.of(2023, 2, 1, 14, 30))
                .updateDate(LocalDateTime.of(2023, 2, 1, 14, 30))
                .build();

        // Act
        ClientDetailResponse result = clientMapper.toDetailResponse(client);

        // Assert - Basic fields should be mapped
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Bob");
        assertThat(result.getLastName()).isEqualTo("Johnson");
        assertThat(result.getAge()).isEqualTo(40);
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1983, 12, 10));
        assertThat(result.getFullName()).isEqualTo("Bob Johnson");
        assertThat(result.getCreationDate()).isEqualTo(LocalDateTime.of(2023, 2, 1, 14, 30));
        assertThat(result.getUpdateDate()).isEqualTo(LocalDateTime.of(2023, 2, 1, 14, 30));

        // Assert - Derived fields should be null (ignored by mapper)
        assertThat(result.getCalculatedCurrentAge()).isNull();
        assertThat(result.getEstimatedRetirementDate()).isNull();
        assertThat(result.getEstimatedLifeExpectancy()).isNull();
        assertThat(result.getYearsToRetirement()).isNull();
        assertThat(result.getEstimatedRemainingYears()).isNull();
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void mappers_WithNullInput_ShouldReturnNull() {
        // Act & Assert
        assertThat(clientMapper.toEntity(null)).isNull();
        assertThat(clientMapper.toResponse(null)).isNull();
        assertThat(clientMapper.toDetailResponse(null)).isNull();
    }

    @Test
    @DisplayName("Should handle partial data correctly")
    void toEntity_WithPartialData_ShouldMapAvailableFields() {
        // Arrange
        ClientCreateRequest request = ClientCreateRequest.builder()
                .name("Alice")
                .lastName("Brown")
                .age(28)
                // birthDate is null
                .build();

        // Act
        Client result = clientMapper.toEntity(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Brown");
        assertThat(result.getAge()).isEqualTo(28);
        assertThat(result.getBirthDate()).isNull();
    }

    @Test
    @DisplayName("Should handle full name generation correctly")
    void toResponse_ShouldGenerateFullNameCorrectly() {
        // Arrange
        Client client1 = Client.builder()
                .name("John")
                .lastName("Doe")
                .build();

        Client client2 = Client.builder()
                .name("Maria")
                .lastName("García López")
                .build();

        // Act
        ClientResponse result1 = clientMapper.toResponse(client1);
        ClientResponse result2 = clientMapper.toResponse(client2);

        // Assert
        assertThat(result1.getFullName()).isEqualTo("John Doe");
        assertThat(result2.getFullName()).isEqualTo("Maria García López");
    }

    @Test
    @DisplayName("Should map creation and update dates correctly")
    void toResponse_ShouldMapTimestampsCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earlier = now.minusDays(1);
        
        Client client = Client.builder()
                .name("Test")
                .lastName("User")
                .creationDate(earlier)
                .updateDate(now)
                .build();

        // Act
        ClientResponse result = clientMapper.toResponse(client);

        // Assert
        assertThat(result.getCreationDate()).isEqualTo(earlier);
        assertThat(result.getUpdateDate()).isEqualTo(now);
    }

    @Test
    @DisplayName("Mapper should be Spring bean")
    void mapper_ShouldBeSpringBean() {
        // Assert
        assertThat(clientMapper).isNotNull();
        assertThat(clientMapper).isInstanceOf(ClientMapper.class);
    }
}
