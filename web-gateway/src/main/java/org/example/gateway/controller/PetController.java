package org.example.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.dto.PetDTO;
import org.example.gateway.model.User;
import org.example.gateway.service.PetService;
import org.example.gateway.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Slf4j
public class PetController {
    private final PetService petService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<PetDTO>> getAllPets() {
        log.info("Getting all pets");
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @petService.isPetOwner(#id, authentication)")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        log.info("Getting pet by id: {}", id);
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @petService.isPetOwner(null, authentication)")
    public ResponseEntity<PetDTO> createPet(@RequestBody PetDTO petDTO, Authentication authentication) {
        log.info("Creating new pet: {}", petDTO);
        User user = userService.findByUsername(authentication.getName());
        petDTO.setOwnerId(user.getOwnerId());
        return ResponseEntity.ok(petService.createPet(petDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @petService.isPetOwner(#id, authentication)")
    public ResponseEntity<PetDTO> updatePet(@PathVariable Long id, @RequestBody PetDTO petDTO) {
        log.info("Updating pet with id: {}", id);
        return ResponseEntity.ok(petService.updatePet(id, petDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @petService.isPetOwner(#id, authentication)")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        log.info("Deleting pet with id: {}", id);
        petService.deletePet(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/connect")
    @PreAuthorize("hasRole('ADMIN') or @petService.isPetOwner(#petId, authentication)")
    public ResponseEntity<PetDTO> addFriendToPet(@RequestParam Long petId, @RequestParam Long friendId) {
        log.info("Adding friend {} to pet {}", friendId, petId);
        return ResponseEntity.ok(petService.addFriendToPet(petId, friendId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PetDTO>> getMyPets(Authentication authentication) {
        log.info("Getting all pets for current user");
        return ResponseEntity.ok(petService.getMyPets());
    }
} 