package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.OwnerDTO;
import org.example.dto.PetDTO;
import org.example.service.OwnerService;
import org.example.service.PetService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Tag(name = "Owner", description = "Owner management APIs")
public class OwnerController {
    private final OwnerService ownerService;
    private final PetService petService;

    @PostMapping
    @Operation(summary = "Create new owner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OwnerDTO> createOwner(@RequestBody OwnerDTO ownerDTO) {
        if (ownerDTO.getDateOfBirth() == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ownerDTO.getName() == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        OwnerDTO createdOwner = ownerService.create(ownerDTO);
        return ResponseEntity.ok(createdOwner);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get owner by ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OwnerDTO> getOwnerById(
            @Parameter(description = "Owner ID", required = true)
            @PathVariable Long id) {
        OwnerDTO ownerDTO = ownerService.getById(id);
        return ResponseEntity.ok(ownerDTO);
    }

    @GetMapping("/pets")
    @Operation(summary = "Get all my pets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PetDTO>> getPetsByOwnerId(@RequestParam(required = false) Long id) {
        List<PetDTO> petDTOs = petService.getPetsByOwnerId(id);
        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping
    @Operation(summary = "Get all owners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OwnerDTO>> getAllOwners(
            @RequestParam(required = false) String name,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date_of_birthday
    ) {
        List<OwnerDTO> owners = ownerService.getAll(null, null, name, date_of_birthday, null, null);
        return ResponseEntity.ok(owners);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update owner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OwnerDTO> updateOwner(
            @Parameter(description = "Owner ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Owner data", required = true)
            @RequestBody OwnerDTO ownerDTO) {
        OwnerDTO updatedOwner = ownerService.update(ownerDTO, id);
        return ResponseEntity.ok(updatedOwner);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete owner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOwner(
            @Parameter(description = "Owner ID", required = true)
            @PathVariable Long id) {
        ownerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}