package br.com.microservices.orchestrated.orderservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.start-saga}")
    private String startSagaTopic;

    public void sendEvent(String playload) {
        try {
            log.info("Sending event to kafka {} with payload {}", startSagaTopic, playload);
            kafkaTemplate.send(startSagaTopic, playload);
        } catch (Exception e) {
            log.error("Error sending event to kafka", e);
        }
    }


}
