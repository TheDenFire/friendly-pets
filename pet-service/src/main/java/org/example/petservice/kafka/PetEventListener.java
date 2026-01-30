package org.example.petservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.petservice.dto.PetDto;
import org.example.petservice.entity.Color;
import org.example.petservice.service.PetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PetEventListener {
    private final PetService petService;

    @KafkaListener(topics = "pet-events", groupId = "pet-service-group")
    public void handlePetEvent(ConsumerRecord<String, Map<String, Object>> record) {
        String correlationId = record.key();
        Map<String, Object> event = record.value();
        log.info("Received pet event: {} with correlationId: {}", event, correlationId);
        try {
            String type = (String) event.get("type");
            switch (type) {
                case "GET_ALL" -> {
                    var pets = petService.getAllPets();
                    petService.completeResponse(correlationId, pets);
                }
                case "GET" -> {
                    Long id = ((Number) event.get("id")).longValue();
                    var pet = petService.getPetById(id);
                    petService.completeResponse(correlationId, pet);
                }
                case "CREATE" -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) event.get("data");
                    PetDto petDto = new PetDto();
                    petDto.setName((String) data.get("name"));
                    petDto.setType((String) data.get("type"));
                    petDto.setOwnerId(((Number) data.get("ownerId")).longValue());
                    if (data.get("birthday") != null) {
                        List<Integer> birthday = (List<Integer>) data.get("birthday");
                        petDto.setBirthday(LocalDate.of(birthday.get(0), birthday.get(1), birthday.get(2)));
                    }
                    petDto.setBreed((String) data.get("breed"));
                    if (data.get("color") != null) {
                        petDto.setColor(Color.valueOf((String) data.get("color")));
                    }
                    var pet = petService.createPet(petDto);
                    petService.completeResponse(correlationId, pet);
                }
                case "UPDATE" -> {
                    Long id = ((Number) event.get("id")).longValue();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) event.get("data");
                    PetDto petDto = new PetDto();
                    petDto.setName((String) data.get("name"));
                    petDto.setType((String) data.get("type"));
                    petDto.setOwnerId(((Number) data.get("ownerId")).longValue());
                    var pet = petService.updatePet(id, petDto);
                    petService.completeResponse(correlationId, pet);
                }
                case "DELETE" -> {
                    Long id = ((Number) event.get("id")).longValue();
                    petService.deletePet(id);
                    petService.completeResponse(correlationId, null);
                }
                case "ADD_FRIEND" -> {
                    Long petId = ((Number) event.get("petId")).longValue();
                    Long friendId = ((Number) event.get("friendId")).longValue();
                    var pet = petService.addFriend(petId, friendId);
                    petService.completeResponse(correlationId, pet);
                }
                default -> {
                    log.error("Unknown event type: {}", type);
                    petService.completeResponse(correlationId, new RuntimeException("Unknown event type: " + type));
                }
            }
        } catch (Exception e) {
            log.error("Error processing pet event", e);
            petService.completeResponse(correlationId, e);
        }
    }
} 