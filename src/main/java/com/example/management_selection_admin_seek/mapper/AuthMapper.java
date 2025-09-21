package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.dto.auth.LoginResponse;
import com.example.management_selection_admin_seek.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Authentication Response Mapper - JWT Response Construction
 * 
 * This mapper handles transformation of authentication data into API responses:
 */
@Mapper(componentModel = "spring")
public interface AuthMapper {

    /**
     * Maps User entity to UserInfo DTO using MapStruct generation
     * Let MapStruct generate the mapping code automatically
     */
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    LoginResponse.UserInfo toUserInfo(User user);

    /**
     * Helper method for building LoginResponse with tokens
     * Still needs to be manual because it involves multiple parameters
     */
    default LoginResponse toLoginResponse(User user, String accessToken, String refreshToken, long expiresIn) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(toUserInfo(user))  // ✅ Now uses MapStruct-generated method
                .build();
    }

    /**
     * Helper method for refresh token response
     * Manual because it involves multiple parameters not from single entity
     */
    default LoginResponse toRefreshResponse(String accessToken, String refreshToken, long expiresIn) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}
