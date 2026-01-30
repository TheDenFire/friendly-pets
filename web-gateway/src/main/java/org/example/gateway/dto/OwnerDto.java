package org.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OwnerDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private List<Long> petIds;
    private Long userId;
} 