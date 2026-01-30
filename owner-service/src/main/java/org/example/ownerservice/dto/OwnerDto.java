package org.example.ownerservice.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private List<Long> petIds;
    private Long userId;
}