package org.example.gateway.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.gateway.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetEventListenerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetEventListener petEventListener;

    private ConsumerRecord<String, Object> createConsumerRecord(String key, Object value) {
        return new ConsumerRecord<>("topic", 0, 0, key, value);
    }

    @Test
    void handlePetEventWithMapResponse() {
        String correlationId = "test-correlation-id";
        Map<String, Object> response = new HashMap<>();
        response.put("id", 1L);
        response.put("name", "TestPet");
        ConsumerRecord<String, Object> record = createConsumerRecord(correlationId, response);

        petEventListener.handlePetEvent(record);

        verify(petService).completeResponse(eq(correlationId), any());
    }

    @Test
    void handlePetEventWithListResponse() {
        String correlationId = "test-correlation-id";
        List<Object> response = List.of(
            Map.of("id", 1L, "name", "Pet1"),
            Map.of("id", 2L, "name", "Pet2")
        );
        ConsumerRecord<String, Object> record = createConsumerRecord(correlationId, response);

        petEventListener.handlePetEvent(record);

        verify(petService).completeResponse(eq(correlationId), any());
    }

    @Test
    void handlePetEventWithError() {
        String correlationId = "test-correlation-id";
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Test error");
        ConsumerRecord<String, Object> record = createConsumerRecord(correlationId, response);

        petEventListener.handlePetEvent(record);

        verify(petService).completeResponse(eq(correlationId), eq(response));
    }

    @Test
    void handlePetEvent_WithUnexpectedType() {
        String correlationId = "test-correlation-id";
        String unexpectedResponse = "unexpected type";
        ConsumerRecord<String, Object> record = createConsumerRecord(correlationId, unexpectedResponse);

        petEventListener.handlePetEvent(record);

        verify(petService).completeResponse(eq(correlationId), any());
    }
} 