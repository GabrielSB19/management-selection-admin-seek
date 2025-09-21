package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.dto.auth.RegisterRequest;
import com.example.management_selection_admin_seek.dto.auth.RegisterResponse;
import com.example.management_selection_admin_seek.entity.User;
import com.example.management_selection_admin_seek.enums.Role;
import com.example.management_selection_admin_seek.mapper.UserMapper;
import com.example.management_selection_admin_seek.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service - Authentication and User Management
 * 
 * This service serves dual purposes in the authentication system:
 * 
 * AS UserDetailsService (Spring Security Integration):
 * - Implements UserDetailsService interface for Spring Security
 * - Loads user details during authentication (login process)
 * - Used by JwtAuthenticationFilter to validate token ownership
 * - Provides user authorities (roles) for authorization decisions
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Load user by username for Spring Security authentication
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Loading user by identifier: {}", identifier);
        
        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

        log.debug("User loaded successfully: {}", user.getUsername());
        return user;
    }

    /**
     * Register a new user account
     */
    public RegisterResponse registerUser(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Validate username and email are unique
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user entity using mapper
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        // Save user to database
        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Build response using mapper
        return userMapper.toRegisterResponse(savedUser);
    }
}
