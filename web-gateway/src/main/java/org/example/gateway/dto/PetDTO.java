package org.example.gateway.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PetDTO {
    private Long id;
    private String name;
    private String type;
    private Long ownerId;
    private LocalDate birthday;
    private String breed;
    private String color;
    private List<Long> friends;
    private Integer version;
} 