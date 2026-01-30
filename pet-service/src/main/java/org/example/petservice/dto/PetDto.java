package org.example.petservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.petservice.entity.Color;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
    private Long id;
    private String name;
    private String type;
    private Long ownerId;
    private LocalDate birthday;
    private String breed;
    private Color color;
    private Set<Long> friendIds;
    private Integer version;
} 