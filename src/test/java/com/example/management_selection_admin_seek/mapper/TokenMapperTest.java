package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for TokenMapper
 * Validates utility methods for JWT token generation
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Token Mapper Tests")
class TokenMapperTest {

    @Autowired
    private TokenMapper tokenMapper;

    @Test
    @DisplayName("Should extract authorities from UserDetails as string list")
    void mapAuthorities_ShouldConvertAuthoritiesToStringList() {
        // Arrange - Use real User entity instead of mock
        User user = User.builder()
                .username("test_user")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        List<String> result = tokenMapper.mapAuthorities(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should handle empty authorities collection")
    void mapAuthorities_WithEmptyAuthorities_ShouldReturnEmptyList() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        // Act
        List<String> result = tokenMapper.mapAuthorities(userDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should extract USER role from User entity")
    void mapUserRole_WithUserRole_ShouldReturnUserString() {
        // Arrange
        User user = User.builder()
                .username("regular_user")
                .role(Role.USER)
                .build();

        // Act
        String result = tokenMapper.mapUserRole(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should extract ADMIN role from User entity")
    void mapUserRole_WithAdminRole_ShouldReturnAdminString() {
        // Arrange
        User user = User.builder()
                .username("admin_user")
                .role(Role.ADMIN)
                .build();

        // Act
        String result = tokenMapper.mapUserRole(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should handle admin user correctly")
    void mapAuthorities_WithAdminUser_ShouldReturnAdminRole() {
        // Arrange - Use real Admin User
        User adminUser = User.builder()
                .username("admin_user")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        // Act
        List<String> result = tokenMapper.mapAuthorities(adminUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should handle disabled user correctly")
    void mapAuthorities_WithDisabledUser_ShouldStillReturnRole() {
        // Arrange - Test disabled user still has authorities
        User disabledUser = User.builder()
                .username("disabled_user")
                .role(Role.USER)
                .enabled(false)
                .build();

        // Act
        List<String> result = tokenMapper.mapAuthorities(disabledUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should work with real User entity as UserDetails")
    void mapAuthorities_WithRealUserEntity_ShouldWork() {
        // Arrange
        User user = User.builder()
                .username("test_user")
                .password("encoded_password")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act - User implements UserDetails
        List<String> result = tokenMapper.mapAuthorities(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should work with ADMIN User entity as UserDetails")
    void mapAuthorities_WithAdminUserEntity_ShouldWork() {
        // Arrange
        User adminUser = User.builder()
                .username("admin_user")
                .password("encoded_password")
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        // Act - User implements UserDetails
        List<String> result = tokenMapper.mapAuthorities(adminUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("ROLE_ADMIN");
    }


    @Test
    @DisplayName("Mapper should be Spring bean")
    void mapper_ShouldBeSpringBean() {
        // Assert
        assertThat(tokenMapper).isNotNull();
        assertThat(tokenMapper).isInstanceOf(TokenMapper.class);
    }
}
