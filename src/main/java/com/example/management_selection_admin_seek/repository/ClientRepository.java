package com.example.management_selection_admin_seek.repository;

import com.example.management_selection_admin_seek.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Client entity
 * Extends JpaRepository for automatic CRUD operations
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    // Spring Data JPA automatically provides:
    // - save(Client client)
    // - findById(Long id)
    // - findAll()
    // - delete(Client client)
    // - etc.
    
    // Custom queries for metrics calculation
    
    /**
     * Calculate average age of all clients
     */
    @Query("SELECT AVG(c.age) FROM Client c")
    Double findAverageAge();
    
    /**
     * Get all ages for statistical calculations
     */
    @Query("SELECT c.age FROM Client c ORDER BY c.age")
    List<Integer> findAllAges();
    
    /**
     * Find minimum age
     */
    @Query("SELECT MIN(c.age) FROM Client c")
    Integer findMinAge();
    
    /**
     * Find maximum age
     */
    @Query("SELECT MAX(c.age) FROM Client c")
    Integer findMaxAge();
}
