package org.example.gateway.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.gateway.service.PetService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PetEventListener {
    private final PetService petService;

    @KafkaListener(topics = "pet-events-response", groupId = "web-gateway-group")
    public void handlePetEvent(ConsumerRecord<String, Object> record) {
        String correlationId = record.key();
        Object value = record.value();
        log.info("Received response for correlationId {}: {}", correlationId, value);
        
        if (value instanceof Map) {
            Map<String, Object> response = (Map<String, Object>) value;
            if (response.containsKey("error")) {
                petService.completeResponse(correlationId, response);
            } else {
                petService.completeResponse(correlationId, Map.of("data", response));
            }
        } else if (value instanceof List) {
            petService.completeResponse(correlationId, Map.of("data", value));
        } else {
            log.error("Unexpected response type: {}", value.getClass());
            petService.completeResponse(correlationId, Map.of("error", "Unexpected response type"));
        }
    }
} 