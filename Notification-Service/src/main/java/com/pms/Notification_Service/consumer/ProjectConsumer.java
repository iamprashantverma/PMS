package com.pms.Notification_Service.consumer;

import com.pms.Notification_Service.event.ProjectEvent;
import com.pms.Notification_Service.event.TaskEvent;
import com.pms.Notification_Service.service.NotificationService;
import com.pms.Notification_Service.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectConsumer {
    private final EmailService emailService;
    private final NotificationService notificationService;


    @KafkaListener(topics = "project-topic", groupId = "project-group")
    public void consumeProjectEvents(ProjectEvent event) {
        log.info("Task Event successfully received: {}", event);
        /* Route to the appropriate method based on the event type */

    }

    private void route(ProjectEvent projectEvent) {

        switch (projectEvent.getEventType()) {
            case PROJECT_CREATED -> notificationService.projectCreatedHandler(projectEvent);
            case PROJECT_UPDATED -> notificationService.projectUpdatedHandler(projectEvent);
            case PROJECT_COMPLETED -> notificationService.projectCompletedHandler(projectEvent);
            case PRIORITY_UPDATED -> notificationService.priorityUpdatedHandler(projectEvent);
            case MEMBER_ASSIGNED -> notificationService.memberAssignedHandler(projectEvent);
            case MEMBER_REMOVED -> notificationService.memberRemovedHandler(projectEvent);
            case STATUS_UPDATED -> notificationService.statusUpdatedHandler(projectEvent);
            case PROJECT_DEADLINE_EXTENDED -> notificationService.projectDeadlineExtendedHandler(projectEvent);
            default -> log.warn("Unknown Project Event Type: {}", projectEvent.getEventType());
        }

    }

}
