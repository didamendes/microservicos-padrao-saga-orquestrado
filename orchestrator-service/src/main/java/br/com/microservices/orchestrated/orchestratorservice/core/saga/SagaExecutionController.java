package br.com.microservices.orchestrated.orchestratorservice.core.saga;

import br.com.microservices.orchestrated.orchestratorservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static br.com.microservices.orchestrated.orchestratorservice.core.saga.SagaHandler.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    public ETopics getNextTopic(Event event) {
        if (isEmpty(event.getStatus()) || isEmpty(event.getSource())) {
            throw new ValidationException("Source and status must be informed !");
        }

        var topic = findTopicBySourceAndStatus(event);
        logCurrentSaga(event, topic);
        return topic;
    }

    private ETopics findTopicBySourceAndStatus(Event event) {
        return (ETopics) (Arrays.stream(SAGA_HANDLER)
                .filter(row -> isEventSourceAndStatusValid(event, row))
                .map(i -> i[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new ValidationException("Topic not found")));
    }

    private boolean isEventSourceAndStatusValid(Event event, Object[] row) {
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];
        return event.getSource().equals(source) && event.getStatus().equals(status);
    }

    private void logCurrentSaga(Event event, ETopics topic) {
        var sagaId = createSagaId(event);
        var source = event.getSource();
        switch (event.getStatus()) {
            case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC: {} | {}", source, topic, sagaId);
            case ROLLBACK_PENDING -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC: {} | {}", source, topic, sagaId);
            case FAIL -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC: {} | {}", source, topic, sagaId);
        }
    }

    private String createSagaId(Event event) {
        return String.format("TRANSACTION ID: %s | EVENT ID: %s",
                 event.getTransactionId(), event.getId());
    }

}
