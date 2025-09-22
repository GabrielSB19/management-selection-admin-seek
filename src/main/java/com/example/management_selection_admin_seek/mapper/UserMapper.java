package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.dto.auth.RegisterRequest;
import com.example.management_selection_admin_seek.dto.auth.RegisterResponse;
import com.example.management_selection_admin_seek.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * User Entity Mapper - Registration and User Data Transformation
 * 
 * This mapper handles transformations between User entity and authentication DTOs:
 * 
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts RegisterRequest to User entity
     * Password encoding and role setting must be done separately
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Will be encoded separately
    @Mapping(target = "role", ignore = true) // Will be set to USER separately
    @Mapping(target = "enabled", ignore = true) // Will be set to true separately
    User toEntity(RegisterRequest request);

    /**
     * Converts User entity to RegisterResponse.UserInfo
     * Maps fullName using the entity's getFullName() method
     * Maps role using the enum's name() method
     */
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    RegisterResponse.UserInfo toUserInfo(User user);

    /**
     * Converts User entity to complete RegisterResponse
     * Uses toUserInfo() method for the user field
     */
    @Mapping(target = "message", constant = "User registered successfully")
    @Mapping(target = "user", source = "user")
    RegisterResponse toRegisterResponse(User user);
}
