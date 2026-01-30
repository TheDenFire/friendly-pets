package org.example.ownerservice.service;

import lombok.RequiredArgsConstructor;
import org.example.ownerservice.entity.Owner;
import org.example.ownerservice.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
    }

    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner updateOwner(Long id, Owner owner) {
        Owner existingOwner = getOwnerById(id);
        existingOwner.setName(owner.getName());
        return ownerRepository.save(existingOwner);
    }

    public void deleteOwner(Long id) {
        ownerRepository.deleteById(id);
    }

    public Owner getOwnerByUserId(Long userId) {
        return ownerRepository.findByUserId(userId);
    }
} 