package com.pms.Notification_Service.consumer;


import com.pms.Notification_Service.service.NotificationService;
import com.pms.Notification_Service.service.impl.NotificationServiceImpl;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import jakarta.mail.MessagingException;
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
    public void consumeTaskTopicEvents(TaskEvent taskEvent) throws MessagingException {
        log.info("Task Event successfully received: {}", taskEvent);
        routeTaskTopicEvent(taskEvent);
    }

    private void routeTaskTopicEvent(TaskEvent taskEvent) throws MessagingException {

        Actions action = taskEvent.getAction();
        switch (action) {

            case UPDATED -> notificationService.taskTopicUpdateHandler(taskEvent);
            case DELETED -> notificationService.taskTopicDeletionHandler(taskEvent);
            case STATUS_CHANGED -> notificationService.taskTopicStatusUpdateHandler(taskEvent);
            case ASSIGNED -> notificationService.taskTopicMemberAssignedHandler(taskEvent);
            case UNASSIGNED -> notificationService.taskTopicMemberUnassignedHandler(taskEvent);
            case PRIORITY_CHANGED -> notificationService.taskTopicPriorityUpdatedHandler(taskEvent);
            default -> log.warn("Unhandled Task Action: {}", action);
        }
    }
}
