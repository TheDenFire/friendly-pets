package org.example.petservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "petEvents", groupId = "pet-service-group")
    public void listener(ConsumerRecord<String, String> record) {
        var event = record.value();
        System.out.println("<UNK>" + event.toString());
        return;
    }
}
