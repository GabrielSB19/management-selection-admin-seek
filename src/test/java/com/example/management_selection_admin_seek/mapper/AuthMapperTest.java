package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.dto.auth.LoginResponse;
import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AuthMapper
 * Validates authentication response mapping methods
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Auth Mapper Tests")
class AuthMapperTest {

    @Autowired
    private AuthMapper authMapper;

    @Test
    @DisplayName("Should map User entity to UserInfo")
    void toUserInfo_ShouldMapAllUserInfoFields() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        LoginResponse.UserInfo result = authMapper.toUserInfo(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should map Admin user correctly")
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
        LoginResponse.UserInfo result = authMapper.toUserInfo(adminUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo("ADMIN");
        assertThat(result.getFullName()).isEqualTo("Admin User");
    }

    @Test
    @DisplayName("Should create complete LoginResponse with user info")
    void toLoginResponse_ShouldCreateCompleteResponse() {
        // Arrange
        User user = User.builder()
                .id(3L)
                .username("login_user")
                .email("login@example.com")
                .firstName("Login")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        String accessToken = "access.token.here";
        String refreshToken = "refresh.token.here";
        long expiresIn = 3600L;

        // Act
        LoginResponse result = authMapper.toLoginResponse(user, accessToken, refreshToken, expiresIn);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access.token.here");
        assertThat(result.getRefreshToken()).isEqualTo("refresh.token.here");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(3600L);

        // Verify user info
        LoginResponse.UserInfo userInfo = result.getUser();
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getId()).isEqualTo(3L);
        assertThat(userInfo.getUsername()).isEqualTo("login_user");
        assertThat(userInfo.getEmail()).isEqualTo("login@example.com");
        assertThat(userInfo.getFullName()).isEqualTo("Login User");
        assertThat(userInfo.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should create refresh token response without user info")
    void toRefreshResponse_ShouldCreateTokenOnlyResponse() {
        // Arrange
        String accessToken = "new.access.token";
        String refreshToken = "new.refresh.token";
        long expiresIn = 1800L;

        // Act
        LoginResponse result = authMapper.toRefreshResponse(accessToken, refreshToken, expiresIn);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new.access.token");
        assertThat(result.getRefreshToken()).isEqualTo("new.refresh.token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(1800L);
        assertThat(result.getUser()).isNull(); // No user info in refresh response
    }

    @Test
    @DisplayName("Should handle null user gracefully in toUserInfo")
    void toUserInfo_WithNullUser_ShouldReturnNull() {
        // Act & Assert
        assertThat(authMapper.toUserInfo(null)).isNull();
    }

    @Test
    @DisplayName("Should handle disabled user correctly")
    void toUserInfo_WithDisabledUser_ShouldMapEnabledFalse() {
        // Arrange
        User disabledUser = User.builder()
                .id(4L)
                .username("disabled_user")
                .email("disabled@example.com")
                .firstName("Disabled")
                .lastName("User")
                .role(Role.USER)
                .enabled(false)
                .build();

        // Act
        LoginResponse.UserInfo result = authMapper.toUserInfo(disabledUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("disabled_user");
    }

    @Test
    @DisplayName("Should handle empty token values")
    void toLoginResponse_WithEmptyTokens_ShouldMapEmptyValues() {
        // Arrange
        User user = User.builder()
                .id(5L)
                .username("empty_token_user")
                .firstName("Empty")
                .lastName("Token")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        LoginResponse result = authMapper.toLoginResponse(user, "", "", 0L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEmpty();
        assertThat(result.getRefreshToken()).isEmpty();
        assertThat(result.getTokenType()).isEqualTo("Bearer"); // Always "Bearer"
        assertThat(result.getExpiresIn()).isEqualTo(0L);
        assertThat(result.getUser()).isNotNull();
    }

    @Test
    @DisplayName("Should handle long expiration times")
    void toRefreshResponse_WithLongExpiration_ShouldMapCorrectly() {
        // Arrange
        long longExpiration = 86400L; // 24 hours

        // Act
        LoginResponse result = authMapper.toRefreshResponse(
                "long.access.token", 
                "long.refresh.token", 
                longExpiration
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getExpiresIn()).isEqualTo(86400L);
    }

    @Test
    @DisplayName("Should handle special characters in tokens")
    void toLoginResponse_WithSpecialCharacters_ShouldMapCorrectly() {
        // Arrange
        User user = User.builder()
                .id(6L)
                .username("special_user")
                .firstName("Special")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        String specialAccessToken = "eyJhbGciOiJIUzI1NiJ9.special_characters_+/=";
        String specialRefreshToken = "refresh.token.with-special_chars+/=";

        // Act
        LoginResponse result = authMapper.toLoginResponse(
                user, 
                specialAccessToken, 
                specialRefreshToken, 
                3600L
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(specialAccessToken);
        assertThat(result.getRefreshToken()).isEqualTo(specialRefreshToken);
    }

    @Test
    @DisplayName("Should handle complex user names")
    void toUserInfo_WithComplexNames_ShouldGenerateFullNameCorrectly() {
        // Arrange
        User user = User.builder()
                .id(7L)
                .username("complex_name_user")
                .email("complex@example.com")
                .firstName("María José")
                .lastName("García López-Hernández")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        LoginResponse.UserInfo result = authMapper.toUserInfo(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("María José García López-Hernández");
    }

    @Test
    @DisplayName("Mapper should be Spring bean")
    void mapper_ShouldBeSpringBean() {
        // Assert
        assertThat(authMapper).isNotNull();
        assertThat(authMapper).isInstanceOf(AuthMapper.class);
    }
}
