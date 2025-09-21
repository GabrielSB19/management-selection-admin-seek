package com.example.management_selection_admin_seek.repository;

import com.example.management_selection_admin_seek.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Simple Repository interface for User entity
 * Basic CRUD operations and essential queries for authentication
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username or email for flexible login
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Check if username exists (for registration validation)
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists (for registration validation)
     */
    boolean existsByEmail(String email);
}
