package org.example.petservice.repository;

import org.example.petservice.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>,
        JpaSpecificationExecutor<Pet> {
    List<Pet> findByOwnerId(Long ownerId);
}
