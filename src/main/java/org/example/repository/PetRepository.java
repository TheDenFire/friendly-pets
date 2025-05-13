package org.example.repository;

import org.example.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PetRepository extends
        JpaRepository<Pet, Long>,
        JpaSpecificationExecutor<Pet> {
    List<Pet> findByOwnerId(Long ownerId);
}
