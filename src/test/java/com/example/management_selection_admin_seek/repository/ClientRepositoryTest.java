package com.example.management_selection_admin_seek.repository;

import com.example.management_selection_admin_seek.entity.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for ClientRepository
 * Tests custom queries and JPA operations
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Client Repository Tests")
class ClientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    private Client client1;
    private Client client2;
    private Client client3;

    @BeforeEach
    void setUp() {
        // Create test clients with different ages
        client1 = Client.builder()
                .name("John")
                .lastName("Doe")
                .age(25)
                .birthDate(LocalDate.of(1999, 5, 15))
                .build();

        client2 = Client.builder()
                .name("Jane")
                .lastName("Smith")
                .age(30)
                .birthDate(LocalDate.of(1994, 8, 20))
                .build();

        client3 = Client.builder()
                .name("Bob")
                .lastName("Johnson")
                .age(35)
                .birthDate(LocalDate.of(1989, 12, 10))
                .build();

        // Persist test data
        entityManager.persistAndFlush(client1);
        entityManager.persistAndFlush(client2);
        entityManager.persistAndFlush(client3);
    }

    @Test
    @DisplayName("Should calculate average age correctly")
    void findAverageAge_ShouldReturnCorrectAverage() {
        // Act
        Double averageAge = clientRepository.findAverageAge();

        // Assert
        assertThat(averageAge).isNotNull();
        assertThat(averageAge).isEqualTo(30.0); // (25 + 30 + 35) / 3 = 30
    }

    @Test
    @DisplayName("Should return null average when no clients exist")
    void findAverageAge_EmptyTable_ShouldReturnNull() {
        // Arrange - Clear all clients
        clientRepository.deleteAll();
        entityManager.flush();

        // Act
        Double averageAge = clientRepository.findAverageAge();

        // Assert
        assertThat(averageAge).isNull();
    }

    @Test
    @DisplayName("Should return all ages in ascending order")
    void findAllAges_ShouldReturnAgesInOrder() {
        // Act
        List<Integer> ages = clientRepository.findAllAges();

        // Assert
        assertThat(ages).isNotNull();
        assertThat(ages).hasSize(3);
        assertThat(ages).containsExactly(25, 30, 35); // Should be ordered
    }

    @Test
    @DisplayName("Should return empty list when no clients exist")
    void findAllAges_EmptyTable_ShouldReturnEmptyList() {
        // Arrange - Clear all clients
        clientRepository.deleteAll();
        entityManager.flush();

        // Act
        List<Integer> ages = clientRepository.findAllAges();

        // Assert
        assertThat(ages).isNotNull();
        assertThat(ages).isEmpty();
    }

    @Test
    @DisplayName("Should find minimum age correctly")
    void findMinAge_ShouldReturnMinimumAge() {
        // Act
        Integer minAge = clientRepository.findMinAge();

        // Assert
        assertThat(minAge).isNotNull();
        assertThat(minAge).isEqualTo(25);
    }

    @Test
    @DisplayName("Should return null min age when no clients exist")
    void findMinAge_EmptyTable_ShouldReturnNull() {
        // Arrange - Clear all clients
        clientRepository.deleteAll();
        entityManager.flush();

        // Act
        Integer minAge = clientRepository.findMinAge();

        // Assert
        assertThat(minAge).isNull();
    }

    @Test
    @DisplayName("Should find maximum age correctly")
    void findMaxAge_ShouldReturnMaximumAge() {
        // Act
        Integer maxAge = clientRepository.findMaxAge();

        // Assert
        assertThat(maxAge).isNotNull();
        assertThat(maxAge).isEqualTo(35);
    }

    @Test
    @DisplayName("Should return null max age when no clients exist")
    void findMaxAge_EmptyTable_ShouldReturnNull() {
        // Arrange - Clear all clients
        clientRepository.deleteAll();
        entityManager.flush();

        // Act
        Integer maxAge = clientRepository.findMaxAge();

        // Assert
        assertThat(maxAge).isNull();
    }

    @Test
    @DisplayName("Should save and retrieve client correctly")
    void save_ShouldPersistClient() {
        // Arrange
        Client newClient = Client.builder()
                .name("Alice")
                .lastName("Brown")
                .age(28)
                .birthDate(LocalDate.of(1996, 3, 15))
                .build();

        // Act
        Client savedClient = clientRepository.save(newClient);
        entityManager.flush();
        entityManager.clear();

        Client retrievedClient = clientRepository.findById(savedClient.getId()).orElse(null);

        // Assert
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isNotNull();
        assertThat(retrievedClient).isNotNull();
        assertThat(retrievedClient.getName()).isEqualTo("Alice");
        assertThat(retrievedClient.getLastName()).isEqualTo("Brown");
        assertThat(retrievedClient.getAge()).isEqualTo(28);
        assertThat(retrievedClient.getBirthDate()).isEqualTo(LocalDate.of(1996, 3, 15));
    }

    @Test
    @DisplayName("Should handle single client scenario")
    void statisticsQueries_WithSingleClient_ShouldWorkCorrectly() {
        // Arrange - Clear existing clients and add one
        clientRepository.deleteAll();
        Client singleClient = Client.builder()
                .name("Solo")
                .lastName("Client")
                .age(40)
                .birthDate(LocalDate.of(1984, 1, 1))
                .build();
        entityManager.persistAndFlush(singleClient);

        // Act
        Double averageAge = clientRepository.findAverageAge();
        List<Integer> allAges = clientRepository.findAllAges();
        Integer minAge = clientRepository.findMinAge();
        Integer maxAge = clientRepository.findMaxAge();

        // Assert
        assertThat(averageAge).isEqualTo(40.0);
        assertThat(allAges).containsExactly(40);
        assertThat(minAge).isEqualTo(40);
        assertThat(maxAge).isEqualTo(40);
    }

    @Test
    @DisplayName("Should count clients correctly")
    void count_ShouldReturnCorrectCount() {
        // Act
        long count = clientRepository.count();

        // Assert
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should find all clients")
    void findAll_ShouldReturnAllClients() {
        // Act
        List<Client> clients = clientRepository.findAll();

        // Assert
        assertThat(clients).hasSize(3);
        assertThat(clients).extracting(Client::getName)
                .containsExactlyInAnyOrder("John", "Jane", "Bob");
    }
}
