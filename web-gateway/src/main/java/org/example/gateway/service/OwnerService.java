package org.example.gateway.service;

import lombok.RequiredArgsConstructor;
import org.example.gateway.dto.OwnerDto;
import org.example.gateway.dto.PetDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, CompletableFuture<Object>> responseFutures = new ConcurrentHashMap<>();

    public List<OwnerDto> getAllOwners() {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("owner-events", correlationId, Map.of(
            "type", "GET_ALL"
        ));

        try {
            Object response = future.get(5, TimeUnit.SECONDS);
            if (response instanceof List) {
                return (List<OwnerDto>) response;
            }
            throw new RuntimeException("Unexpected response");
        } catch (Exception e) {
            throw new RuntimeException("Timeout > 5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public OwnerDto getOwnerById(Long id) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("owner-events", correlationId, Map.of(
            "type", "GET",
            "id", id
        ));

        try {
            Object response = future.get(5, TimeUnit.SECONDS);
            if (response instanceof OwnerDto) {
                return (OwnerDto) response;
            }
            throw new RuntimeException("Unexpected response");
        } catch (Exception e) {
            throw new RuntimeException("Timeout > 5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public OwnerDto createOwner(OwnerDto owner) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("owner-events", correlationId, Map.of(
            "type", "CREATE",
            "data", owner
        ));

        try {
            Object response = future.get(5, TimeUnit.SECONDS);
            if (response instanceof RuntimeException) {
                throw (RuntimeException) response;
            }
            if (response instanceof OwnerDto) {
                return (OwnerDto) response;
            }
            throw new RuntimeException("Unexpected response");
        } catch (Exception e) {
            throw new RuntimeException("Timeout waiting >5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public OwnerDto updateOwner(Long id, OwnerDto owner) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("owner-events", correlationId, Map.of(
            "type", "UPDATE",
            "id", id,
            "data", owner
        ));

        try {
            Object response = future.get(5, TimeUnit.SECONDS);
            if (response instanceof OwnerDto) {
                return (OwnerDto) response;
            }
            throw new RuntimeException("Unexpected response");
        } catch (Exception e) {
            throw new RuntimeException("Timeout >5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public void deleteOwner(Long id) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("owner-events", correlationId, Map.of(
            "type", "DELETE",
            "id", id
        ));

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Timeout >5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public List<PetDTO> getMyPets() {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        responseFutures.put(correlationId, future);

        kafkaTemplate.send("owner-events", correlationId, Map.of(
            "type", "GET_MY_PETS"
        ));

        try {
            Object response = future.get(5, TimeUnit.SECONDS);
            if (response instanceof List) {
                return (List<PetDTO>) response;
            }
            throw new RuntimeException("Unexpected response");
        } catch (Exception e) {
            throw new RuntimeException("Timeout >5 sec", e);
        } finally {
            responseFutures.remove(correlationId);
        }
    }

    public void completeResponse(String correlationId, Object response) {
        CompletableFuture<Object> future = responseFutures.get(correlationId);
        if (future != null) {
            future.complete(response);
        } else {
            System.err.println("No future found for correlationId: " + correlationId);
            System.err.println("Current futures: " + responseFutures.keySet());
        }
    }
} 