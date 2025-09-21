package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.dto.auth.RegisterRequest;
import com.example.management_selection_admin_seek.dto.auth.RegisterResponse;
import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import com.example.management_selection_admin_seek.exception.DuplicateResourceException;
import com.example.management_selection_admin_seek.mapper.UserMapper;
import com.example.management_selection_admin_seek.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * Tests user registration, authentication, and UserDetailsService functionality
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest validRegisterRequest;
    private User validUser;
    private RegisterResponse expectedResponse;

    @BeforeEach
    void setUp() {
        validRegisterRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        validUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        expectedResponse = RegisterResponse.builder()
                .message("User registered successfully")
                .user(RegisterResponse.UserInfo.builder()
                        .id(1L)
                        .username("testuser")
                        .email("test@example.com")
                        .fullName("Test User")
                        .role("USER")
                        .enabled(true)
                        .build())
                .build();
    }

    @Test
    @DisplayName("Should register user successfully with valid data")
    void registerUser_ValidData_ShouldReturnRegisterResponse() {
        // Arrange
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(validRegisterRequest)).thenReturn(validUser);
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        when(userMapper.toRegisterResponse(validUser)).thenReturn(expectedResponse);

        // Act
        RegisterResponse result = userService.registerUser(validRegisterRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("User registered successfully");
        assertThat(result.getUser().getUsername()).isEqualTo("testuser");
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");

        // Verify interactions
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toRegisterResponse(validUser);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when username already exists")
    void registerUser_ExistingUsername_ShouldThrowDuplicateResourceException() {
        // Arrange
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(validRegisterRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User already exists with username: testuser");

        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void registerUser_ExistingEmail_ShouldThrowDuplicateResourceException() {
        // Arrange
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(validRegisterRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User already exists with email: test@example.com");

        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void loadUserByUsername_ExistingUsername_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("testuser"))
                .thenReturn(Optional.of(validUser));

        // Act
        UserDetails result = userService.loadUserByUsername("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encoded_password");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");

        verify(userRepository).findByUsernameOrEmail("testuser");
    }

    @Test
    @DisplayName("Should load user by email successfully")
    void loadUserByUsername_ExistingEmail_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("test@example.com"))
                .thenReturn(Optional.of(validUser));

        // Act
        UserDetails result = userService.loadUserByUsername("test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encoded_password");

        verify(userRepository).findByUsernameOrEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_NonExistingUser_ShouldThrowUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("nonexistent"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: nonexistent");

        verify(userRepository).findByUsernameOrEmail("nonexistent");
    }

    @Test
    @DisplayName("Should set user properties correctly during registration")
    void registerUser_ShouldSetUserPropertiesCorrectly() {
        // Arrange
        User capturedUser = User.builder().build();
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any())).thenReturn(capturedUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        when(userMapper.toRegisterResponse(any())).thenReturn(expectedResponse);

        // Act
        userService.registerUser(validRegisterRequest);

        // Assert - verify that user properties are set correctly
        verify(userRepository).save(argThat(user -> {
            assertThat(user.getPassword()).isEqualTo("encoded_password");
            assertThat(user.getRole()).isEqualTo(Role.USER);
            assertThat(user.getEnabled()).isTrue();
            return true;
        }));
    }
}
