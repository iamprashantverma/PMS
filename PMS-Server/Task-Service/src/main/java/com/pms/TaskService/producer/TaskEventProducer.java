package com.pms.TaskService.producer;

import com.pms.TaskService.event.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.pms.TaskService.configs.KafkaConfig.KAFKA_TASK_SERVICE_TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskEventProducer {

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    /* Sending the new task event to Kafka */
    public void sendTaskEvent(TaskEvent task) {
        kafkaTemplate.send(KAFKA_TASK_SERVICE_TOPIC,task.getEntityId(),task);
    }
}
