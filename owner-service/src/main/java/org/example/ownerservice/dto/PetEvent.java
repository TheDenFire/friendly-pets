package org.example.ownerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetEvent {
    private Long petId;
    private Long ownerId;
    private String eventType; // CREATED, UPDATED, DELETED
    private String petName;
    private String breed;
    private String color;
} 