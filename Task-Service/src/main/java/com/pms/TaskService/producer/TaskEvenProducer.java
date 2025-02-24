package com.pms.TaskService.producer;

import com.pms.TaskService.event.TaskEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.pms.TaskService.configs.KafkaConfig.KAFKA_TASK_SERVICE_TOPIC;

@Service
@Slf4j
public class TaskEvenProducer {

        private KafkaTemplate<String, TaskEvent> kafkaTemplate;

        /* sending the new task event to the kafka */
        public void sendTaskEvent(TaskEvent taskEvent) {
            kafkaTemplate.send(KAFKA_TASK_SERVICE_TOPIC, taskEvent.getTaskId(), taskEvent);
            log.info("event sent");
        }

}
