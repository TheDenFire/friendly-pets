package org.example.gateway.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gateway.dto.OwnerDto;
import org.example.gateway.service.OwnerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OwnerEventListener {
    private final OwnerService ownerService;

    @KafkaListener(topics = "owner-events-response", groupId = "gateway-group")
    public void handleOwnerEvent(@Payload Map<String, Object> event, @Header("kafka_receivedMessageKey") String correlationId) {
        log.info("Received owner event response: {} with correlationId: {}", event, correlationId);
        
        try {
            String type = (String) event.get("type");
            Object data = event.get("data");
            
            switch (type) {
                case "GET_ALL" -> {
                    if (data instanceof Map) {
                        Map<String, Object> ownerData = (Map<String, Object>) data;
                        OwnerDto ownerDto = new OwnerDto();
                        ownerDto.setId(((Number) ownerData.get("id")).longValue());
                        ownerDto.setName((String) ownerData.get("name"));
                        ownerService.completeResponse(correlationId, ownerDto);
                    }
                }
                case "GET" -> {
                    if (data instanceof Map) {
                        Map<String, Object> ownerData = (Map<String, Object>) data;
                        OwnerDto ownerDto = new OwnerDto();
                        ownerDto.setId(((Number) ownerData.get("id")).longValue());
                        ownerDto.setName((String) ownerData.get("name"));
                        ownerService.completeResponse(correlationId, ownerDto);
                    }
                }
                case "CREATE" -> {
                    if (data instanceof Map) {
                        Map<String, Object> ownerData = (Map<String, Object>) data;
                        OwnerDto ownerDto = new OwnerDto();
                        ownerDto.setId(((Number) ownerData.get("id")).longValue());
                        ownerDto.setName((String) ownerData.get("name"));
                        ownerService.completeResponse(correlationId, ownerDto);
                    }
                }
                case "UPDATE" -> {
                    if (data instanceof Map) {
                        Map<String, Object> ownerData = (Map<String, Object>) data;
                        OwnerDto ownerDto = new OwnerDto();
                        ownerDto.setId(((Number) ownerData.get("id")).longValue());
                        ownerDto.setName((String) ownerData.get("name"));
                        ownerService.completeResponse(correlationId, ownerDto);
                    }
                }
                case "DELETE" -> {
                    ownerService.completeResponse(correlationId, null);
                }
                case "GET_MY_PETS" -> {
                    ownerService.completeResponse(correlationId, data);
                }
                default -> {
                    log.error("Unknown event type: {}", type);
                    ownerService.completeResponse(correlationId, new RuntimeException("Unknown event type: " + type));
                }
            }
        } catch (Exception e) {
            log.error("Error processing owner event", e);
            ownerService.completeResponse(correlationId, e);
        }
    }
} 