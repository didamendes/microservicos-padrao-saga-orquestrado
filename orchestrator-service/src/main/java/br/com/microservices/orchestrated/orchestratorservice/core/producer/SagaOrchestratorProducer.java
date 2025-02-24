package br.com.microservices.orchestrated.orchestratorservice.core.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(String playload, String topic) {
        try {
            log.info("Sending event to kafka {} with payload {}", topic, playload);
            kafkaTemplate.send(topic, playload);
        } catch (Exception e) {
            log.error("Error sending event to kafka", e);
        }
    }


}
