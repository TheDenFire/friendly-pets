package org.example.petservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.petservice.dto.PetDto;
import org.example.petservice.entity.Pet;
import org.example.petservice.repository.PetRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {
    private final PetRepository petRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, CompletableFuture<Object>> responseMap = new ConcurrentHashMap<>();

    @Transactional
    public PetDto createPet(PetDto petDto) {
        log.info("Creating pet with data: {}", petDto);
        Pet pet = new Pet();
        pet.setName(petDto.getName());
        pet.setOwnerId(petDto.getOwnerId());
        pet.setBirthday(petDto.getBirthday());
        pet.setBreed(petDto.getBreed());
        pet.setColor(petDto.getColor());
        pet = petRepository.save(pet);
        log.info("Created pet: {}", pet);
        return convertToDTO(pet);
    }

    private PetDto convertToDTO(Pet pet) {
        return new PetDto(
            pet.getId(),
            pet.getName(),
            pet.getType(),
            pet.getOwnerId(),
            pet.getBirthday(),
            pet.getBreed(),
            pet.getColor(),
            pet.getFriends().stream().map(Pet::getId).collect(Collectors.toSet()),
            pet.getVersion()
        );
    }

    public void completeResponse(String correlationId, Object response) {
        log.info("Sending response for correlationId {}: {}", correlationId, response);
        kafkaTemplate.send("pet-events-response", correlationId, response);
    }

    @Transactional(readOnly = true)
    public List<PetDto> getAllPets() {
        return petRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PetDto getPetById(Long id) {
        return petRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
    }

    @Transactional
    public PetDto updatePet(Long id, PetDto petDto) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
        pet.setName(petDto.getName());
        pet.setType(petDto.getType());
        pet.setOwnerId(petDto.getOwnerId());
        pet.setBirthday(petDto.getBirthday());
        pet.setBreed(petDto.getBreed());
        pet.setColor(petDto.getColor());
        pet = petRepository.save(pet);
        return convertToDTO(pet);
    }

    @Transactional
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    @Transactional
    public PetDto addFriend(Long petId, Long friendId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
        Pet friend = petRepository.findById(friendId)
            .orElseThrow(() -> new RuntimeException("Friend not found with id: " + friendId));
        pet.getFriends().add(friend);
        pet = petRepository.save(pet);
        return convertToDTO(pet);
    }

    @Transactional(readOnly = true)
    public List<PetDto> getPetsByOwnerId(Long ownerId) {
        log.info("Getting pets for owner: {}", ownerId);
        List<Pet> pets = petRepository.findByOwnerId(ownerId);
        return pets.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
} 