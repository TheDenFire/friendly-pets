package org.example.ownerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ownerservice.dto.PetEvent;
import org.example.ownerservice.entity.Owner;
import org.example.ownerservice.repository.OwnerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetEventListener {
    private final OwnerRepository ownerRepository;

    @KafkaListener(
        topics = "petEvents",
        groupId = "owner-service-group",
        properties = {
            "spring.json.value.default.type=org.example.ownerservice.dto.PetEvent"
        }
    )
    @Transactional
    public void handlePetEvent(PetEvent event) {
        log.info("Received pet event: {}", event);
        
        Owner owner = ownerRepository.findById(event.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + event.getOwnerId()));
        
        switch (event.getEventType()) {
            case "CREATED":
                handlePetCreated(owner, event);
                break;
            case "UPDATED":
                handlePetUpdated(owner, event);
                break;
            case "DELETED":
                handlePetDeleted(owner, event);
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void handlePetCreated(Owner owner, PetEvent event) {
        log.info("Handling pet created event for owner: {}", owner.getId());
        owner.getPetIds().add(event.getPetId());
        ownerRepository.save(owner);
        log.info("Added pet {} to owner {}", event.getPetId(), owner.getId());
    }

    private void handlePetUpdated(Owner owner, PetEvent event) {
        log.info("Handling pet updated event for owner: {}", owner.getId());
        // If the pet was transferred to a different owner
        if (!owner.getPetIds().contains(event.getPetId())) {
            owner.getPetIds().add(event.getPetId());
            ownerRepository.save(owner);
            log.info("Added pet {} to owner {}", event.getPetId(), owner.getId());
        }
    }

    private void handlePetDeleted(Owner owner, PetEvent event) {
        log.info("Handling pet deleted event for owner: {}", owner.getId());
        owner.getPetIds().remove(event.getPetId());
        ownerRepository.save(owner);
        log.info("Removed pet {} from owner {}", event.getPetId(), owner.getId());
    }
} 