package com.example.management_selection_admin_seek.mapper;

import com.example.management_selection_admin_seek.entity.User;
import org.mapstruct.Mapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Token Claims Mapper - JWT Token Generation Support
 * 
 * This utility mapper extracts and transforms user data for JWT token generation:
 * 
 */
@Mapper(componentModel = "spring")
public interface TokenMapper {

    /**
     * Extract authorities as list of strings
     * Default method needed because UserDetails.getAuthorities() returns Collection<GrantedAuthority>
     * and we need to transform it to List<String>
     */
    default List<String> mapAuthorities(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    /**
     * Extract user role as string
     * Default method needed because we need to call .name() on enum
     */
    default String mapUserRole(User user) {
        return user.getRole().name();
    }
}
