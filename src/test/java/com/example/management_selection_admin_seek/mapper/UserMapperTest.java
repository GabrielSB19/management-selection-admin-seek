package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.dto.auth.RegisterRequest;
import com.example.management_selection_admin_seek.dto.auth.RegisterResponse;
import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for UserMapper
 * Validates MapStruct generated mapping methods for user registration
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("User Mapper Tests")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("Should convert RegisterRequest to User entity")
    void toEntity_ShouldMapBasicFieldsAndIgnoreSecurityFields() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("plainPassword123")
                .confirmPassword("plainPassword123")
                .firstName("John")
                .lastName("Doe")
                .build();

        // Act
        User result = userMapper.toEntity(request);

        // Assert - Mapped fields
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");

        // Assert - Ignored fields behavior
        assertThat(result.getId()).isNull();
        assertThat(result.getPassword()).isNull(); // Will be encoded separately
        
        // Note: Even though these fields are ignored in mapping, @Builder.Default still applies
        assertThat(result.getRole()).isEqualTo(Role.USER); // @Builder.Default takes effect
        assertThat(result.isEnabled()).isTrue(); // @Builder.Default takes effect
    }

    @Test
    @DisplayName("Should convert User entity to UserInfo")
    void toUserInfo_ShouldMapAllUserInfoFields() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("jane_smith")
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        RegisterResponse.UserInfo result = userMapper.toUserInfo(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("jane_smith");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        assertThat(result.getFullName()).isEqualTo("Jane Smith");
        assertThat(result.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should handle ADMIN role correctly in UserInfo mapping")
    void toUserInfo_WithAdminRole_ShouldMapRoleCorrectly() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .username("admin_user")
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        // Act
        RegisterResponse.UserInfo result = userMapper.toUserInfo(adminUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo("ADMIN");
        assertThat(result.getFullName()).isEqualTo("Admin User");
    }

    @Test
    @DisplayName("Should convert User entity to complete RegisterResponse")
    void toRegisterResponse_ShouldCreateCompleteResponse() {
        // Arrange
        User user = User.builder()
                .id(3L)
                .username("new_user")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        RegisterResponse result = userMapper.toRegisterResponse(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("User registered successfully");
        
        // Verify nested UserInfo
        RegisterResponse.UserInfo userInfo = result.getUser();
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getId()).isEqualTo(3L);
        assertThat(userInfo.getUsername()).isEqualTo("new_user");
        assertThat(userInfo.getEmail()).isEqualTo("new@example.com");
        assertThat(userInfo.getFullName()).isEqualTo("New User");
        assertThat(userInfo.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void mappers_WithNullInput_ShouldReturnNull() {
        // Act & Assert
        assertThat(userMapper.toEntity(null)).isNull();
        assertThat(userMapper.toUserInfo(null)).isNull();
        assertThat(userMapper.toRegisterResponse(null)).isNull();
    }

    @Test
    @DisplayName("Should handle partial RegisterRequest correctly")
    void toEntity_WithPartialData_ShouldMapAvailableFields() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("partial_user")
                .email("partial@example.com")
                .firstName("Partial")
                // lastName is null
                .password("password123")
                .confirmPassword("password123")
                .build();

        // Act
        User result = userMapper.toEntity(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("partial_user");
        assertThat(result.getEmail()).isEqualTo("partial@example.com");
        assertThat(result.getFirstName()).isEqualTo("Partial");
        assertThat(result.getLastName()).isNull();
    }

    @Test
    @DisplayName("Should handle user with minimal data correctly")
    void toUserInfo_WithMinimalData_ShouldMapBasicFields() {
        // Arrange
        User user = User.builder()
                .id(4L)
                .username("minimal_user")
                .email("minimal@example.com")
                .firstName("Minimal")
                .lastName("User")
                .role(Role.USER) // Valid role
                .enabled(false)
                .build();

        // Act
        RegisterResponse.UserInfo result = userMapper.toUserInfo(user);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getUsername()).isEqualTo("minimal_user");
        assertThat(result.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should handle empty names correctly")
    void toUserInfo_WithEmptyNames_ShouldHandleFullName() {
        // Arrange
        User user = User.builder()
                .id(5L)
                .username("empty_names")
                .email("empty@example.com")
                .firstName("")
                .lastName("")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        RegisterResponse.UserInfo result = userMapper.toUserInfo(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo(" "); // Empty first + space + empty last
        assertThat(result.getUsername()).isEqualTo("empty_names");
    }

    @Test
    @DisplayName("Should handle long names correctly")
    void toUserInfo_WithLongNames_ShouldMapCorrectly() {
        // Arrange
        User user = User.builder()
                .id(6L)
                .username("long_name_user")
                .email("long@example.com")
                .firstName("Very Long First Name")
                .lastName("Very Long Last Name With Multiple Words")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        RegisterResponse.UserInfo result = userMapper.toUserInfo(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("Very Long First Name Very Long Last Name With Multiple Words");
    }

    @Test
    @DisplayName("Mapper should be Spring bean")
    void mapper_ShouldBeSpringBean() {
        // Assert
        assertThat(userMapper).isNotNull();
        assertThat(userMapper).isInstanceOf(UserMapper.class);
    }
}
