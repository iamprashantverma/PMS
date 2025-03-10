package com.pms.projectservice.producer;

import com.pms.projectservice.event.ProjectEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.pms.projectservice.configs.KafkaConfig.KAFKA_PROJECT_SERVICE_TOPIC;

@Service
@RequiredArgsConstructor
public class ProjectEventProducer {

    private final  KafkaTemplate<String, ProjectEvent> kafkaTemplate;

    /* produce to the kafka */
    public void sendProjectEvent(ProjectEvent projectEvent) {
        kafkaTemplate.send(KAFKA_PROJECT_SERVICE_TOPIC,projectEvent.getProjectId(),projectEvent);
    }

}
