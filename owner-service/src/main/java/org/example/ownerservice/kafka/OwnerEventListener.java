package org.example.ownerservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ownerservice.entity.Owner;
import org.example.ownerservice.service.OwnerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class OwnerEventListener {
    private final OwnerService ownerService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "owner-events", groupId = "owner-service-group")
    public void handleOwnerEvent(@Payload Map<String, Object> message, @Header("kafka_receivedMessageKey") String key) {
        log.info("Received message: {} with key: {}", message, key);
        String type = (String) message.get("type");
        Object response = null;

        switch (type) {
            case "GET" -> {
                Long id = ((Number) message.get("id")).longValue();
                Owner owner = ownerService.getOwnerById(id);
                response = convertToMap(owner);
            }
            case "CREATE" -> {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) message.get("data");
                    log.info("Processing CREATE request with data: {}", data);
                    String name = (String) data.get("name");
                    LocalDate dateOfBirthday = LocalDate.parse((String) data.get("dateOfBirthday"));
                    Long userId = Long.valueOf(data.get("userId").toString());

                    // Check if owner already exists for this user
                    if (ownerService.getOwnerByUserId(userId) != null) {
                        log.info("Owner already exists for user: {}", userId);
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("operation", "CREATE_RESPONSE");
                        errorResponse.put("status", "ERROR");
                        errorResponse.put("message", "Owner already exists for this user");
                        kafkaTemplate.send("owner-events-response", key, errorResponse);
                        return;
                    }

                    Owner owner = new Owner();
                    owner.setName(name);
                    owner.setDateOfBirth(dateOfBirthday);
                    owner.setUserId(userId);
                    owner = ownerService.createOwner(owner);
                    
                    Map<String, Object> successResponse = convertToMap(owner);
                    successResponse.put("operation", "CREATE_RESPONSE");
                    successResponse.put("status", "SUCCESS");
                    log.info("Sending success response: {}", successResponse);
                    kafkaTemplate.send("owner-events-response", key, successResponse);
                } catch (Exception e) {
                    log.error("Error processing CREATE request", e);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("operation", "CREATE_RESPONSE");
                    errorResponse.put("status", "ERROR");
                    errorResponse.put("message", e.getMessage());
                    kafkaTemplate.send("owner-events-response", key, errorResponse);
                }
            }
            case "UPDATE" -> {
                Long id = ((Number) message.get("id")).longValue();
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) message.get("data");
                Owner owner = new Owner();
                owner.setName((String) data.get("name"));
                owner.setDateOfBirth(java.time.LocalDate.parse((String) data.get("dateOfBirth")));
                if (data.containsKey("userId")) {
                    owner.setUserId(((Number) data.get("userId")).longValue());
                }
                owner = ownerService.updateOwner(id, owner);
                response = convertToMap(owner);
            }
            case "DELETE" -> {
                Long id = ((Number) message.get("id")).longValue();
                ownerService.deleteOwner(id);
                response = "Owner deleted successfully";
            }
        }

        if (response != null) {
            log.info("Sending response: {}", response);
            kafkaTemplate.send("owner-events-response", key, response);
        }
    }

    private Map<String, Object> convertToMap(Owner owner) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", owner.getId());
        map.put("name", owner.getName());
        map.put("dateOfBirthday", owner.getDateOfBirth().toString());
        if (owner.getUserId() != null) {
            map.put("userId", owner.getUserId());
        }
        return map;
    }
} 