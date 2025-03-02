package com.pms.Notification_Service.consumer;

import com.pms.Notification_Service.event.TaskEvent;
import com.pms.Notification_Service.event.enums.Actions;
import com.pms.Notification_Service.service.impl.NotificationServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskConsumer {

    private final NotificationServiceImpl notificationService;

    @KafkaListener(topics = "task-topic", groupId = "task-group")
    public void consumeTaskTopicEvents(TaskEvent taskEvent) {
        log.info("Task Event successfully received: {}", taskEvent);

        /* Route to the appropriate method based on the event type */
        routeTaskTopicEvent(taskEvent);
    }

    private void routeTaskTopicEvent(TaskEvent taskEvent) {
        Actions action = taskEvent.getAction();
        switch (action) {
            case CREATED -> notificationService.taskTopicCreationHandler(taskEvent);
            case UPDATED -> notificationService.taskTopicUpdateHandler(taskEvent);
            case DELETED -> notificationService.taskTopicDeletionHandler(taskEvent);
            case STATUS_CHANGED -> notificationService.taskTopicStatusUpdateHandler(taskEvent);
            case ASSIGNED -> notificationService.taskTopicMemberAssignedHandler(taskEvent);
            case UNASSIGNED -> notificationService.taskTopicMemberUnassignedHandler(taskEvent);
            case PRIORITY_CHANGED -> notificationService.taskTopicPriorityUpdatedHandler(taskEvent);
//            case COMMENTED -> notificationService.taskTopicCommentAddedHandler(taskEvent);
//            case ATTACHMENT_ADDED -> notificationService.taskTopicAttachmentAddedHandler(taskEvent);
//            case ATTACHMENT_REMOVED -> notificationService.taskTopicAttachmentRemovedHandler(taskEvent);

//            case TAG_ADDED -> notificationService.taskTopicTagAddedHandler(taskEvent);
//            case TAG_REMOVED -> notificationService.taskTopicTagRemovedHandler(taskEvent);
//            case LINKED -> notificationService.taskTopicLinkedHandler(taskEvent);
//            case UNLINKED -> notificationService.taskTopicUnlinkedHandler(taskEvent);
//            case RESOLVED -> notificationService.taskTopicResolvedHandler(taskEvent);
//            case REOPENED -> notificationService.taskTopicReopenedHandler(taskEvent);
//            case CLOSED -> notificationService.taskTopicClosedHandler(taskEvent);
            default -> log.warn("Unhandled Task Action: {}", action);
        }
    }
}
