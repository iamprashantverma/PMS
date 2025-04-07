package com.pms.Notification_Service.service.impl;


import com.pms.Notification_Service.clients.UserFeignClient;
import com.pms.Notification_Service.dto.UserDTO;
import com.pms.Notification_Service.entities.UserMessageNotification;
import com.pms.Notification_Service.repositories.UserMessageNotificationRepo;
import com.pms.Notification_Service.service.NotificationService;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import com.pms.projectservice.event.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.MessagingException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl  implements NotificationService {

    private final UserFeignClient userFeignClient;
    private final   EmailService emailService;
    private final ModelMapper modelMapper;
    private final UserMessageNotificationRepo userMessageNotificationRepo;


    @Override
    public void taskTopicUpdateHandler(TaskEvent taskEvent) throws MessagingException {
        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        LocalDate createdDate = taskEvent.getCreatedDate();
        LocalDate deadline = taskEvent.getDeadline();
        String updatedBy = taskEvent.getAssignedBy();
        EventType eventType = taskEvent.getEventType();
        Set<String> assigneeIds = taskEvent.getAssignees();

        // Define subject
        String subject = eventType + " Update Notification: " + title;

        // Iterate over assignee IDs and send emails
        for (String userId : assigneeIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();

            if (user != null && user.getEmail() != null) {
                // Prepare email template variables using HashMap<>
                Map<String, Object> variables = new HashMap<>();
                variables.put("userName", user.getName());
                variables.put("title", title);
                variables.put("description", description);
                variables.put("taskId", entityId);
                variables.put("oldStatus", taskEvent.getOldStatus());
                variables.put("newStatus", taskEvent.getNewStatus());
                variables.put("eventType", eventType);

                // Send the email
                emailService.sendEmail(user.getEmail(), subject, "task-status-update", variables);
                log.info("Email sent to {} for {} update (Task ID: {})", user.getEmail(), eventType, entityId);
            } else {
                log.warn("User with ID {} not found or has no email.", userId);
            }
        }
    }


    @Override
    public void taskTopicStatusUpdateHandler(TaskEvent taskEvent) {

        EventType eventType = taskEvent.getEventType();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        String taskId = taskEvent.getEntityId();
        Status newStatus = taskEvent.getNewStatus();
        Status oldStatus = taskEvent.getOldStatus();

        UserMessageNotification umn = modelMapper.map(taskEvent,UserMessageNotification.class);
        userMessageNotificationRepo.save(umn);
        System.out.println(umn);

        List<UserDTO> assignees = new ArrayList<>();
        String subject = eventType + " Status Update Notification: " + title;
        // Fetch all users from UserService
        for (String userId : taskEvent.getAssignees()) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            assignees.add(user);
        }
        // Send email to each assignee
        for (UserDTO user : assignees) {
            log.info(" i m here 3 user  :{}",user);
            try {
                HashMap<String, Object> variables = new HashMap<>();
                variables.put("userName", user.getName());
                variables.put("title", title);
                variables.put("description", description);
                variables.put("taskId", taskId);
                variables.put("oldStatus", oldStatus);
                variables.put("newStatus", newStatus);
                variables.put("eventType", eventType);

                log.info(" i m here 4 5");
                emailService.sendEmail(user.getEmail(), subject, "task-status-update", variables);
                log.info("Email sent to {} regarding task update for taskId {}", user.getEmail(), taskId);
            } catch (MessagingException e) {
                log.error("Failed to send email to {} for taskId {}", user.getEmail(), taskId, e);
            }
        }
    }

    @Override
    public void taskTopicDeletionHandler(TaskEvent taskEvent) throws MessagingException {
        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        Set<String> memberIds = taskEvent.getAssignees();

        // Subject for the email
        String subject = "Task Deletion Notification: " + title;

        for (String userId : memberIds) {
            // Fetch user details via Feign client
            UserDTO user = userFeignClient.getUserById(userId).getData();

            if (user != null && user.getEmail() != null) {
                // Prepare email template variables
                Map<String, Object> variables = Map.of(
                        "userName", user.getName(),
                        "title", title,
                        "description", description != null ? description : "No description available",
                        "taskId", entityId
                );

                // Send email using Thymeleaf template
                emailService.sendEmail(user.getEmail(), subject, "task-deletion", variables);
                log.info("Email sent to {} for task deletion (Task ID: {})", user.getEmail(), entityId);
            } else {
                log.warn("User with ID {} not found or has no email.", userId);
            }
        }
    }

    @Override
    public void taskTopicMemberAssignedHandler(TaskEvent taskEvent) throws MessagingException {
    String entityId = taskEvent.getEntityId();
    String title = taskEvent.getTitle();
    String description = taskEvent.getDescription();
    EventType eventType = taskEvent.getEventType();
    Priority priority = taskEvent.getPriority();
    Set<String> assigneeIds = taskEvent.getAssignees();

    // Subject for the email
    String subject = "New " + eventType + " Assignment: " + title;

    for (String userId : assigneeIds) {

        // Fetch user details via Feign client
        UserDTO user = userFeignClient.getUserById(userId).getData();

        if (user != null && user.getEmail() != null) {

            // Prepare email template variables
            Map<String, Object> variables = Map.of(
                    "userName", user.getName(),
                    "title", title,
                    "description", description != null ? description : "No description available",
                    "taskId", entityId,
                    "priority", priority,
                    "eventType",eventType
            );

            // Send email using Thymeleaf template
            emailService.sendEmail(user.getEmail(), subject, "task-assigned", variables);
            log.info("Email sent to {} for {} assignment (Task ID: {})", user.getEmail(), eventType, entityId);
        } else {
            log.warn("User with ID {} not found or has no email.", userId);
        }
    }
}

    @Override
    public void taskTopicMemberUnassignedHandler(TaskEvent taskEvent) throws MessagingException {

        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        EventType eventType = taskEvent.getEventType();
        Set<String> memberIds = taskEvent.getAssignees();

        // Subject for the email
        String subject = eventType + " Unassignment Notification: " + title;

        for (String userId : memberIds) {
            // Fetch user details via Feign client
            UserDTO user = userFeignClient.getUserById(userId).getData();

            if (user != null && user.getEmail() != null) {
                // Prepare email template variables
                Map<String, Object> variables = Map.of(
                        "userName", user.getName(),
                        "title", title,
                        "description", description != null ? description : "No description available",
                        "taskId", entityId,
                        "eventType",eventType
                );

                // Send email using Thymeleaf template
                emailService.sendEmail(user.getEmail(), subject, "task-unassigned", variables);
                log.info("Email sent to {} for {} unassignment (Task ID: {})", user.getEmail(), eventType, entityId);
            } else {
                log.warn("User with ID {} not found or has no email.", userId);
            }
        }
    }

    @Override
    public void taskTopicPriorityUpdatedHandler(TaskEvent taskEvent) throws MessagingException {
        String entityId = taskEvent.getEntityId();
        String title = taskEvent.getTitle();
        String description = taskEvent.getDescription();
        EventType eventType = taskEvent.getEventType();
        Priority newPriority = taskEvent.getPriority();
        Set<String> memberIds = taskEvent.getAssignees();

        // Subject for the email
        String subject = "Priority Updated Notification: " + title;

        for (String userId : memberIds) {
            // Fetch user details via Feign client
            UserDTO user = userFeignClient.getUserById(userId).getData();

            if (user != null && user.getEmail() != null) {
                // Prepare email template variables
                Map<String, Object> variables = Map.of(
                        "userName", user.getName(),
                        "title", title,
                        "description", description != null ? description : "No description available",
                        "taskId", entityId,
                        "newPriority", newPriority
                );

                // Send email using Thymeleaf template
                emailService.sendEmail(user.getEmail(), subject, "task-priority-update", variables);
                log.info("Email sent to {} for priority update (Task ID: {})", user.getEmail(), entityId);
            } else {
                log.warn("User with ID {} not found or has no email.", userId);
            }
        }
    }

}
