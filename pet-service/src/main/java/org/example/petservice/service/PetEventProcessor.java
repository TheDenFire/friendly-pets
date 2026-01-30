package org.example.petservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.petservice.dto.PetEvent;
import org.example.petservice.entity.Pet;
import org.example.petservice.repository.PetRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetEventProcessor {
    private final PetRepository petRepository;

    @KafkaListener(
        topics = "petEvents",
        groupId = "pet-service-group",
        properties = {
            "spring.json.value.default.type=org.example.petservice.dto.PetEvent"
        }
    )
    @Transactional
    public void handlePetEvent(PetEvent event) {
        log.info("Received pet event: {}", event);
        
        switch (event.getEventType()) {
            case "CREATED":
                handlePetCreated(event);
                break;
            case "UPDATED":
                handlePetUpdated(event);
                break;
            case "DELETED":
                handlePetDeleted(event);
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void handlePetCreated(PetEvent event) {
        log.info("Creating pet from event: {}", event);
        Pet pet = new Pet();
        pet.setName(event.getPetName());
        pet.setBreed(event.getBreed());
//        pet.setColor(event.getColor());
//        pet.setBirthday(LocalDate.now()); // Можно добавить в событие, если нужно
        pet.setOwnerId(event.getOwnerId());
        petRepository.save(pet);
    }

    private void handlePetUpdated(PetEvent event) {
        log.info("Updating pet from event: {}", event);
        petRepository.findById(event.getPetId()).ifPresent(pet -> {
            pet.setName(event.getPetName());
            pet.setBreed(event.getBreed());
//            pet.setColor(event.getColor());
            pet.setOwnerId(event.getOwnerId());
            petRepository.save(pet);
        });
    }

    private void handlePetDeleted(PetEvent event) {
        log.info("Deleting pet from event: {}", event);
        petRepository.deleteById(event.getPetId());
    }
} 