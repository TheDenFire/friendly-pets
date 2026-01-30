package org.example.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.dto.PetDTO;
import org.example.gateway.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, CompletableFuture<Map<String, Object>>> responseFutures = new ConcurrentHashMap<>();
    private final UserService userService;

    public List<PetDTO> getAllPets() {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("pet-events", correlationId, Map.of(
            "type", "GET_ALL"
        ));

        try {
            Map<String, Object> response = future.get(5, TimeUnit.SECONDS);
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }
            List<Map<String, Object>> pets = (List<Map<String, Object>>) response.get("data");
            return pets.stream()
                .map(this::convertMapToPetDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Timeout 5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public PetDTO getPetById(Long id) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("pet-events", correlationId, Map.of(
            "type", "GET",
            "id", id
        ));

        try {
            Map<String, Object> response = future.get(5, TimeUnit.SECONDS);
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }
            return convertMapToPetDTO((Map<String, Object>) response.get("data"));
        } catch (Exception e) {
            throw new RuntimeException("Timeout 5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public PetDTO createPet(PetDTO petDTO) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("pet-events", correlationId, Map.of(
            "type", "CREATE",
            "data", petDTO
        ));

        try {
            Map<String, Object> response = future.get(5, TimeUnit.SECONDS);
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }
            return convertMapToPetDTO((Map<String, Object>) response.get("data"));
        } catch (Exception e) {
            throw new RuntimeException("Timeout 5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public PetDTO updatePet(Long id, PetDTO petDTO) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("pet-events", correlationId, Map.of(
            "type", "UPDATE",
            "id", id,
            "data", petDTO
        ));

        try {
            Map<String, Object> response = future.get(5, TimeUnit.SECONDS);
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }
            return convertMapToPetDTO((Map<String, Object>) response.get("data"));
        } catch (Exception e) {
            throw new RuntimeException("Timeout >5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public void deletePet(Long id) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("pet-events", correlationId, Map.of(
            "type", "DELETE",
            "id", id
        ));

        try {
            Map<String, Object> response = future.get(5, TimeUnit.SECONDS);
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Timeout >5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public PetDTO addFriendToPet(Long petId, Long friendId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("pet-events", correlationId, Map.of(
            "type", "ADD_FRIEND",
            "petId", petId,
            "friendId", friendId
        ));

        try {
            Map<String, Object> response = future.get(5, TimeUnit.SECONDS);
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }
            return convertMapToPetDTO((Map<String, Object>) response.get("data"));
        } catch (Exception e) {
            throw new RuntimeException("Timeout >5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public List<PetDTO> getMyPets() {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("pet-events", correlationId, Map.of(
            "type", "GET_MY_PETS"
        ));

        try {
            Map<String, Object> response = future.get(5, TimeUnit.SECONDS);
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }
            List<Map<String, Object>> pets = (List<Map<String, Object>>) response.get("data");
            return pets.stream()
                .map(this::convertMapToPetDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Timeout waiting for pet service response", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public void completeResponse(String correlationId, Map<String, Object> response) {
        CompletableFuture<Map<String, Object>> future = responseFutures.get(correlationId);
        if (future != null) {
            future.complete(response);
        } else {
            log.error("No future found for correlationId: {}", correlationId);
            log.error("Current futures: {}", responseFutures.keySet());
        }
    }

    private PetDTO convertMapToPetDTO(Map<String, Object> map) {
        if (map == null) {
            throw new RuntimeException("Received null response from pet service");
        }
        PetDTO dto = new PetDTO();
        dto.setId(((Number) map.get("id")).longValue());
        dto.setName((String) map.get("name"));
        dto.setType((String) map.get("type"));
        dto.setOwnerId(((Number) map.get("ownerId")).longValue());
        
        // Handle birthday as a list [year, month, day]
        if (map.get("birthday") != null) {
            List<Integer> birthday = (List<Integer>) map.get("birthday");
            dto.setBirthday(LocalDate.of(birthday.get(0), birthday.get(1), birthday.get(2)));
        }
        
        dto.setBreed((String) map.get("breed"));
        dto.setColor((String) map.get("color"));
        
        if (map.containsKey("friendIds")) {
            List<Number> friends = (List<Number>) map.get("friendIds");
            dto.setFriends(friends.stream()
                .map(Number::longValue)
                .collect(Collectors.toList()));
        }
        
        if (map.containsKey("version")) {
            dto.setVersion(((Number) map.get("version")).intValue());
        }
        
        return dto;
    }

    public boolean isOwner(Long ownerId, Authentication authentication) {
        log.info("Checking if user is owner of ownerId: {}", ownerId);
        User user = userService.findByUsername(authentication.getName());
        return user != null && user.getOwnerId().equals(ownerId);
    }

    public boolean isPetOwner(Long petId, Authentication authentication) {
        log.info("Checking if user is owner of pet: {}", petId);
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return false;
        }
        if (petId == null) {
            return true;
        }
        PetDTO pet = getPetById(petId);
        return pet != null && pet.getOwnerId().equals(user.getOwnerId());
    }
} 