package org.example.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.dto.OwnerDto;
import org.example.gateway.dto.PetDTO;
import org.example.gateway.service.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {
    private final OwnerService ownerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OwnerDto>> getAllOwners() {
        log.info("Getting all owners");
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownerService.isOwner(#id, authentication)")
    public ResponseEntity<OwnerDto> getOwnerById(@PathVariable Long id) {
        log.info("Getting owner by id: {}", id);
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @GetMapping("/me/pets")
    public ResponseEntity<List<PetDTO>> getMyPets() {
        log.info("Getting pets for current owner");
        return ResponseEntity.ok(ownerService.getMyPets());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OwnerDto> createOwner(@RequestBody OwnerDto ownerDto) {
        log.info("Creating new owner: {}", ownerDto);
        return ResponseEntity.ok(ownerService.createOwner(ownerDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownerService.isOwner(#id, authentication)")
    public ResponseEntity<OwnerDto> updateOwner(@PathVariable Long id, @RequestBody OwnerDto ownerDto) {
        log.info("Updating owner with id: {}", id);
        return ResponseEntity.ok(ownerService.updateOwner(id, ownerDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownerService.isOwner(#id, authentication)")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        log.info("Deleting owner with id: {}", id);
        ownerService.deleteOwner(id);
        return ResponseEntity.ok().build();
    }
} 