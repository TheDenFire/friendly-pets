package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.PetDTO;
import org.example.entity.Color;
import org.example.entity.Pet;
import org.example.entity.User;
import org.example.service.PetService;
import org.example.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pet", description = "Pet management APIs")
public class PetController {
    private final PetService petService;
    private final UserService userService;

//    @GetMapping("/byColor")
//    public ResponseEntity<List<PetDTO>> getPetsByColor(
//            @RequestParam(required = false) Color color,
//            Pageable pageable) {
//            return ResponseEntity.ok(petService.getPetsByColor(color, pageable));
//    }

    @GetMapping
    @Operation(summary = "Get all pets")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PetDTO>> getAllPets(
            @RequestParam(required = false) Color color,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<PetDTO> pets = petService.getAll(
                color,
                breed,
                name,
                birthday,
                startDate,
                endDate
        );
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pet by ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PetDTO> getPetById(
            @Parameter(description = "Pet ID", required = true)
            @PathVariable Long id) {
        PetDTO petDTO = petService.getById(id);
        return ResponseEntity.ok(petDTO);
    }

    @PostMapping
    @Operation(summary = "Create new pet")
    @PreAuthorize("hasRole('ADMIN') or @petService.isOwner(#petDTO.ownerId, authentication)")
    public ResponseEntity<PetDTO> createPet(
            @RequestBody PetDTO petDTO,
            Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        if (currentUser.getOwner() == null) {
            return ResponseEntity.status(403)
                    .body(null);
        }
        petDTO.setOwnerId(currentUser.getOwner().getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(petService.create(petDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pet by owner")
    @PreAuthorize("hasRole('ADMIN') or @petService.isOwner(#id, authentication)")
    public ResponseEntity<PetDTO> updatePetByOwner(
            @Parameter(description = "Pet ID") @PathVariable Long id,
            @RequestBody PetDTO petDTO,
            Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        if (currentUser.getOwner() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        PetDTO pet = petService.getById(id);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }

        if (!pet.getOwnerId().equals(currentUser.getOwner().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(petService.update(petDTO, id));
    }

    @PutMapping("/admin/{id}")
    @Operation(summary = "Update pet by admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetDTO> updatePetByAdmin(
            @Parameter(description = "Pet ID") @PathVariable Long id,
            @RequestBody PetDTO petDTO) {
        return ResponseEntity.ok(petService.update(petDTO, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pet")
    @PreAuthorize("hasRole('ADMIN') or @petService.isOwner(#id, authentication)")
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "Pet ID") @PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{petId}/friends/{friendId}")
    @Operation(summary = "Add friend to pet")
    @PreAuthorize("hasRole('ADMIN') or @petService.isOwner(#petId, authentication)")
    public ResponseEntity<Void> addFriend(
            @Parameter(description = "Pet ID") @PathVariable Long petId,
            @Parameter(description = "Friend ID") @PathVariable Long friendId) {
        petService.addFriend(petId, friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{petId}/owner/{ownerId}")
    @Operation(summary = "Set owner for pet")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetDTO> setOwner(
            @Parameter(description = "Pet ID") @PathVariable Long petId,
            @Parameter(description = "Owner ID") @PathVariable Long ownerId) {
        petService.setOwner(petId, ownerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pets")
    @Operation(summary = "Get all my pets")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PetDTO>> getPetsByOwnerId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("user_id");
        List<PetDTO> petDTOs = petService.getPetsByOwnerId(userId);
        return ResponseEntity.ok(petDTOs);
    }
}