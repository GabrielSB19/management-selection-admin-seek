package com.example.management_selection_admin_seek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for client response
 * Includes complete client information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Long id;
    private String name;
    private String lastName;
    private String fullName;
    private Integer age;
    private LocalDate birthDate;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}
