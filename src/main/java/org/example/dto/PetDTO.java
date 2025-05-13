package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Color;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class PetDTO {
    private Long id;
    private String name;
    private LocalDate birthday;
    private String breed;
    private Color color;
    private Long ownerId;
    private List<Long> friendIds;
}