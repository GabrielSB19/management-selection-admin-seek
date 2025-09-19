package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.dto.ClientCreateRequest;
import com.example.management_selection_admin_seek.dto.ClientResponse;
import com.example.management_selection_admin_seek.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Client entities and DTOs
 * Uses MapStruct for automatic code generation at compile time
 */
@Mapper(componentModel = "spring")
public interface ClientMapper {

    /**
     * Converts ClientCreateRequest to Client entity
     * Fields are automatically mapped by name
     */
    Client toEntity(ClientCreateRequest request);

    /**
     * Converts Client entity to ClientResponse
     * Maps fullName using the entity's getFullName() method
     */
    @Mapping(target = "fullName", source = "fullName")
    ClientResponse toResponse(Client client);
}
