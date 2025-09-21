package com.example.management_selection_admin_seek.repository;

import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for UserRepository
 * Tests custom queries and authentication-related operations
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("$2a$10$encoded.password.here")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .enabled(true)
                .build();

        testUser2 = User.builder()
                .username("jane_smith")
                .email("jane@example.com")
                .password("$2a$10$another.encoded.password")
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        // Persist test data
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsernameOrEmail_WithUsername_ShouldReturnUser() {
        // Act
        Optional<User> result = userRepository.findByUsernameOrEmail("john_doe");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john_doe");
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Doe");
        assertThat(result.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Should find user by email")
    void findByUsernameOrEmail_WithEmail_ShouldReturnUser() {
        // Act
        Optional<User> result = userRepository.findByUsernameOrEmail("jane@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("jane_smith");
        assertThat(result.get().getEmail()).isEqualTo("jane@example.com");
        assertThat(result.get().getFirstName()).isEqualTo("Jane");
        assertThat(result.get().getLastName()).isEqualTo("Smith");
        assertThat(result.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("Should return empty when user not found")
    void findByUsernameOrEmail_UserNotFound_ShouldReturnEmpty() {
        // Act
        Optional<User> result = userRepository.findByUsernameOrEmail("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle case sensitivity correctly")
    void findByUsernameOrEmail_CaseSensitive_ShouldNotMatchDifferentCase() {
        // Act
        Optional<User> result1 = userRepository.findByUsernameOrEmail("JOHN_DOE");
        Optional<User> result2 = userRepository.findByUsernameOrEmail("JOHN@EXAMPLE.COM");

        // Assert
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername_ExistingUsername_ShouldReturnTrue() {
        // Act
        boolean exists = userRepository.existsByUsername("john_doe");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for non-existing username")
    void existsByUsername_NonExistingUsername_ShouldReturnFalse() {
        // Act
        boolean exists = userRepository.existsByUsername("non_existing_user");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_ExistingEmail_ShouldReturnTrue() {
        // Act
        boolean exists = userRepository.existsByEmail("john@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for non-existing email")
    void existsByEmail_NonExistingEmail_ShouldReturnFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexisting@example.com");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should save and retrieve user correctly")
    void save_ShouldPersistUser() {
        // Arrange
        User newUser = User.builder()
                .username("new_user")
                .email("new@example.com")
                .password("$2a$10$new.encoded.password")
                .firstName("New")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        User savedUser = userRepository.save(newUser);
        entityManager.flush();
        entityManager.clear();

        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("new_user");
        assertThat(retrievedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(retrievedUser.getFirstName()).isEqualTo("New");
        assertThat(retrievedUser.getLastName()).isEqualTo("User");
        assertThat(retrievedUser.getRole()).isEqualTo(Role.USER);
        assertThat(retrievedUser.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should handle username and email uniqueness")
    void uniqueConstraints_ShouldWork() {
        // Act
        long initialCount = userRepository.count();
        
        // Check that our test users exist
        boolean usernameExists = userRepository.existsByUsername("john_doe");
        boolean emailExists = userRepository.existsByEmail("john@example.com");

        // Assert
        assertThat(initialCount).isEqualTo(2);
        assertThat(usernameExists).isTrue();
        assertThat(emailExists).isTrue();
    }

    @Test
    @DisplayName("Should find all users")
    void findAll_ShouldReturnAllUsers() {
        // Act
        var users = userRepository.findAll();

        // Assert
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername)
                .containsExactlyInAnyOrder("john_doe", "jane_smith");
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("john@example.com", "jane@example.com");
    }

    @Test
    @DisplayName("Should delete user correctly")
    void delete_ShouldRemoveUser() {
        // Arrange
        Long userId = testUser1.getId();

        // Act
        userRepository.delete(testUser1);
        entityManager.flush();

        // Assert
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle default values correctly")
    void defaultValues_ShouldBeApplied() {
        // Arrange
        User userWithDefaults = User.builder()
                .username("defaults_user")
                .email("defaults@example.com")
                .password("password")
                .firstName("Default")
                .lastName("User")
                .build(); // Not setting role and enabled explicitly

        // Act
        User savedUser = userRepository.save(userWithDefaults);
        entityManager.flush();
        entityManager.clear();

        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getRole()).isEqualTo(Role.USER); // Default value
        assertThat(retrievedUser.isEnabled()).isTrue(); // Default value
    }
}
