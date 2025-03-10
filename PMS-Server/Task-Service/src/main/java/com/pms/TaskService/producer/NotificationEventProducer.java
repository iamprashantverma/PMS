package com.pms.TaskService.producer;

import com.pms.TaskService.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.pms.TaskService.configs.KafkaConfig.KAFKA_NOTIFICATION_SERVICE_TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void sendNotificationEvent(NotificationEvent notificationEvent) {
        log.info("Sending NotificationEvent to Kafka: {}", notificationEvent);
        kafkaTemplate.send(KAFKA_NOTIFICATION_SERVICE_TOPIC, notificationEvent.getId(), notificationEvent);
    }
}
