package com.pms.Notification_Service.service.impl;


import com.pms.Notification_Service.clients.UserFeignClient;
import com.pms.Notification_Service.dto.UserDTO;
import com.pms.Notification_Service.service.NotificationService;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl  implements NotificationService {

    private final UserFeignClient userFeignClient;
    private final EmailService emailService;


    @Override
    public void taskTopicUpdateHandler(TaskEvent taskEvent) {
        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        LocalDate createdDate = taskEvent.getCreatedDate();
        LocalDate deadline = taskEvent.getDeadline();
        String updatedBy = taskEvent.getAssignedBy();
        EventType eventType = taskEvent.getEventType();
        Set<String> assigneeIds = taskEvent.getAssignees();

        for (String userId : assigneeIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null) {
                String subject = eventType + " Update Notification: " + title;
                String body = "Hello " + user.getName() + ",\n\n" +
                        "The " + eventType + " \"" + title + "\" has been updated.\n\n" +
                        "Details:\n" +
                        "--------------------\n" +
                        "Title: " + title + "\n" +
                        "Description: " + description + "\n" +
                        "ID: " + entityId + "\n" +
                        "Created Date: " + createdDate + "\n" +
                        "Deadline: " + deadline + "\n" +
                        "Updated By: " + updatedBy + "\n" +
                        "--------------------\n\n" +
                        "Please log in to your dashboard to view the latest details.\n\n" +
                        "Regards,\n" +
                        "The Task Management Team";
                emailService.sendEmail(user.getEmail(), subject, body);
                log.info("Email sent to {} for {} update (ID: {})", user.getEmail(), eventType, entityId);
            }
        }
    }

    @Override
    public void taskTopicStatusUpdateHandler(TaskEvent taskEvent) {

        EventType eventType = taskEvent.getEventType();
        LocalDate createdDate = taskEvent.getCreatedDate();
        LocalDate deadline = taskEvent.getDeadline();
        String createdBy = taskEvent.getAssignedBy();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        String taskId = taskEvent.getEntityId();
        List<UserDTO> assignees =  new ArrayList<>();

        for (String userId: taskEvent.getAssignees()) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            assignees.add(user);
        }
        // send the email to each and every assignee
        for (UserDTO user : assignees) {
            String subject = eventType +" Status Update Notification: " + title;
            String body = "Dear " + user.getName() + ",\n\n" +
                    "We would like to inform you that the status of the\"" + eventType + "  " + title + "\" has been updated.\n\n" +
                    "Task Details:\n" +
                    "--------------------\n" +
                    "Title:          " + title + "\n" +
                    "Description:    " + description + "\n" +
                    "Status:         " + eventType + "\n" +
                    "Created Date:   " + createdDate + "\n" +
                    "Deadline:       " + deadline + "\n" +
                    eventType + " ID:        " + taskId + "\n" +
                    "Updated By:     " + createdBy + "\n" +
                    "--------------------\n\n" +
                    "Please log in to your dashboard to view the full details and take any necessary actions.\n\n" +
                    "Thank you,\n" +
                    "The Task Management Team";
            emailService.sendEmail(user.getEmail(), subject, body);
            log.info("Email sent to {} regarding task update for taskId {}", user.getEmail(), taskId);
        }

    }

    @Override
    public void taskTopicDeletionHandler(TaskEvent taskEvent) {
        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        Set<String> memberIds = taskEvent.getAssignees();

        for (String userId : memberIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null) {
                String subject = "Task Completion Notification: " + title;
                String body = "Hello " + user.getName() + ",\n\n" +
                        "We would like to inform you that the task \"" + title + "\" has been marked as complete.\n\n" +
                        "Task Details:\n" +
                        "Title: " + title + "\n" +
                        "Description: " + description + "\n" +
                        "ID: " + entityId + "\n\n" +
                        "Please check your dashboard for more details.\n\n" +
                        "Regards,\n" +
                        "The Task Management Team";
                emailService.sendEmail(user.getEmail(), subject, body);
                log.info("Email sent to {} for task completion (ID: {})", user.getEmail(), entityId);
            }
        }
    }


    @Override
    public void taskTopicMemberAssignedHandler(TaskEvent taskEvent) {

        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        EventType eventType = taskEvent.getEventType();
        Set<String> assigneeIds = taskEvent.getAssignees();

        for (String userId : assigneeIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null) {
                String subject = "New " + eventType + " Assignment: " + title;
                String body = "Hello " + user.getName() + ",\n\n" +
                        "You have been assigned to the " + eventType + " \"" + title + "\".\n\n" +
                        eventType + " Details:\n" +
                        "Title: " + title + "\n" +
                        "Description: " + description + "\n" +
                        "ID: " + entityId + "\n\n" +
                        "Please check your dashboard for more details.\n\n" +
                        "Regards,\n" +
                        "The Task Management Team";
                emailService.sendEmail(user.getEmail(), subject, body);
                log.info("Email sent to {} for {} assignment (ID: {})", user.getEmail(), eventType, entityId);
            }
        }
    }


    @Override
    public void taskTopicMemberUnassignedHandler(TaskEvent taskEvent) {
        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        EventType eventType = taskEvent.getEventType();
        Set<String> memberIds = taskEvent.getAssignees();

        for (String userId : memberIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null) {
                String subject = eventType + " Unassignment Notification: " + title;
                String body = "Hello " + user.getName() + ",\n\n" +
                        "You have been unassigned from the " + eventType + " \"" + title + "\".\n\n" +
                        "Details:\n" +
                        "Title: " + title + "\n" +
                        "Description: " + description + "\n" +
                        "ID: " + entityId + "\n\n" +
                        "Please check your dashboard for more details.\n\n" +
                        "Regards,\n" +
                        "The Task Management Team";
                emailService.sendEmail(user.getEmail(), subject, body);
                log.info("Email sent to {} for {} unassignment (ID: {})", user.getEmail(), eventType, entityId);
            }
        }
    }

    @Override
    public void taskTopicPriorityUpdatedHandler(TaskEvent taskEvent) {
        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        EventType eventType = taskEvent.getEventType();
        Priority newPriority = taskEvent.getPriority();
        Set<String> memberIds = taskEvent.getAssignees();

        for (String userId : memberIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null) {
                String subject = "Priority Updated Notification: " + title;
                String body = "Hello " + user.getName() + ",\n\n" +
                        "The priority for the " + eventType + " \"" + title + "\" has been updated.\n\n" +
                        "New Priority: " + newPriority + "\n" +
                        "Task Details:\n" +
                        "Title: " + title + "\n" +
                        "Description: " + description + "\n" +
                        "ID: " + entityId + "\n\n" +
                        "Please check your dashboard for more details.\n\n" +
                        "Regards,\n" +
                        "The Task Management Team";
                emailService.sendEmail(user.getEmail(), subject, body);
                log.info("Email sent to {} for priority update (ID: {})", user.getEmail(), entityId);
            }
        }
    }


}
