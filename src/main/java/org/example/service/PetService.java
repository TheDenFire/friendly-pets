package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dto.PetDTO;
import org.example.entity.Color;
import org.example.entity.Owner;
import org.example.entity.Pet;
import org.example.mapper.PetMapper;
import org.example.repository.OwnerRepository;
import org.example.repository.PetRepository;
import org.example.specification.PetSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PetService implements IService<PetDTO, Long, Pet> {

    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
    private final PetMapper petMapper;

    @Override
    public PetDTO create(PetDTO petDTO) {
        Pet pet = petMapper.toEntity(petDTO);
        if (petDTO.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(petDTO.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
            pet.setOwner(owner);
        }
        Pet savedPet = petRepository.save(pet);
        return petMapper.toDTO(savedPet);
    }

    @Override
    public PetDTO getById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        return petMapper.toDTO(pet);
    }

    @Override
    public List<PetDTO> getAll(Color color,
                             String breed,
                             String name,
                             LocalDate birthday,
                             LocalDate startDate,
                             LocalDate endDate) {
        Specification<Pet> spec = Specification
                .where(PetSpecifications.hasColor(color))
                .and(PetSpecifications.hasBreed(breed))
                .and(PetSpecifications.hasName(name))
                .and(PetSpecifications.hasBirthday(birthday))
                .and(PetSpecifications.birthdayBetween(startDate, endDate));

        List<Pet> pets = petRepository.findAll(spec);
        return pets.stream()
                .map(petMapper::toDTO)
                .toList();
    }

    @Override
    public PetDTO update(PetDTO petDTO, Long id) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        Pet updatedPet = petMapper.toEntity(petDTO);
        updatedPet.setId(existingPet.getId());
        
        if (petDTO.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(petDTO.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
            updatedPet.setOwner(owner);
        }

        Pet savedPet = petRepository.save(updatedPet);
        return petMapper.toDTO(savedPet);
    }

    @Override
    public void delete(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        petRepository.delete(pet);
    }

    public List<PetDTO> getPetsByOwnerId(Long ownerId) {
        List<Pet> pets = petRepository.findByOwnerId(ownerId);
        return pets.stream()
                .map(petMapper::toDTO)
                .toList();
    }

    public void addFriend(Long petId, Long friendId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        Pet friend = petRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        pet.getFriends().add(friend);
        friend.getFriends().add(pet);

        petRepository.save(pet);
        petRepository.save(friend);
    }

    public PetDTO setOwner(Long petId, Long ownerId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        pet.setOwner(owner);
        Pet updatedPet = petRepository.save(pet);

        return petMapper.toDTO(updatedPet);
    }

    public boolean isOwner(Long petId, Authentication authentication) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        
        if (pet.getOwner() == null) {
            return false;
        }

        return pet.getOwner().getUser().getUsername().equals(authentication.getName());
    }
}