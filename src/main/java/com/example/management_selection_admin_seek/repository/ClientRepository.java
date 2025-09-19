package com.example.management_selection_admin_seek.repository;

import com.example.management_selection_admin_seek.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
