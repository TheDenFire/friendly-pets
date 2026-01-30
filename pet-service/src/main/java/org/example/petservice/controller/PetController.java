package org.example.petservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.petservice.dto.PetDto;
import org.example.petservice.entity.Pet;
import org.example.petservice.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Slf4j
public class PetController {
    private final PetService petService;

    @GetMapping
    public ResponseEntity<List<PetDto>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDto> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PetDto>> getPetsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(petService.getPetsByOwnerId(ownerId));
    }

    @PostMapping
    public ResponseEntity<PetDto> createPet(@RequestBody PetDto petDto) {
        log.info("Creating new pet: {}", petDto);
        return ResponseEntity.ok(petService.createPet(petDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDto> updatePet(@PathVariable Long id, @RequestBody PetDto petDto) {
        log.info("Updating pet with id: {}", id);
        return ResponseEntity.ok(petService.updatePet(id, petDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{petId}/friends/{friendId}")
    public ResponseEntity<PetDto> addFriend(@PathVariable Long petId, @PathVariable Long friendId) {
        return ResponseEntity.ok(petService.addFriend(petId, friendId));
    }
} 