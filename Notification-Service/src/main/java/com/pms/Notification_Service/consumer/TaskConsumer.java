package com.pms.Notification_Service.consumer;

import com.pms.Notification_Service.event.TaskEvent;
import com.pms.Notification_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "task-topic", groupId = "task-group")
    public void consumeTaskEvents(TaskEvent taskEvent) {
        log.info("Task Event successfully received: {}", taskEvent);

        /* Route to the appropriate method based on the event type */
        route(taskEvent);
    }

    private void route(TaskEvent taskEvent) {
        switch (taskEvent.getEventType()) {
            case TASK_CREATED -> notificationService.taskCreatedHandler(taskEvent);
            case TASK_UPDATED -> notificationService.taskUpdatedHandler(taskEvent);
            case TASK_COMPLETED -> notificationService.taskCompletedHandler(taskEvent);
            default -> log.warn("Unknown Task Event Type: {}", taskEvent.getEventType());
        }
    }
}
