package com.pms.Notification_Service.service.impl;

import com.pms.Notification_Service.advices.APIResponse;
import com.pms.Notification_Service.auth.UserContextHolder;
import com.pms.Notification_Service.clients.UserFeignClient;
import com.pms.Notification_Service.dto.UserDTO;
import com.pms.Notification_Service.dto.UserMessageNotificationDTO;
import com.pms.Notification_Service.entities.UserMessageNotification;
import com.pms.Notification_Service.repositories.UserMessageNotificationRepo;
import com.pms.Notification_Service.service.NotificationService;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import com.pms.projectservice.event.enums.Status;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserFeignClient userFeignClient;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final UserMessageNotificationRepo userMessageNotificationRepo;

    // Map of user-specific sinks for emitting notifications
    private final Map<String, Sinks.Many<UserMessageNotificationDTO>> userNotificationSinks = new ConcurrentHashMap<>();

    private String getUserId() {
        return UserContextHolder.getCurrentUserId();
    }

    private void sendNotificationUpdate(String userId, UserMessageNotificationDTO notification) {
        Sinks.Many<UserMessageNotificationDTO> sink = userNotificationSinks.get(userId);
        if (sink != null) {
            sink.tryEmitNext(notification);
        }
    }

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

        String subject = eventType + " Update Notification: " + title;

        for (String userId : assigneeIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null && user.getEmail() != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("userName", user.getName());
                variables.put("title", title);
                variables.put("description", description);
                variables.put("taskId", entityId);
                variables.put("oldStatus", taskEvent.getOldStatus());
                variables.put("newStatus", taskEvent.getNewStatus());
                variables.put("eventType", eventType);

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

        for (String userId : taskEvent.getAssignees()) {
           APIResponse<UserDTO> userData  =  userFeignClient.getUserById(userId);
           UserDTO user = userData.getData();
            if(eventType == EventType.TASK && user.getTaskUpdates() != null && !user.getTaskUpdates())
                    continue;
            else if(eventType == EventType.BUG && user.getBugUpdates() != null && !user.getBugUpdates())
                    continue;
            else if(eventType == EventType.SUBTASK && user.getSubTaskUpdates() != null && !user.getSubTaskUpdates())
                continue;
            System.out.println(user);

            UserMessageNotification umn = modelMapper.map(taskEvent, UserMessageNotification.class);
            umn.setUserId(userId);
            UserMessageNotification saved = userMessageNotificationRepo.save(umn);

            // Emit real-time notification to the user
            UserMessageNotificationDTO dto = modelMapper.map(saved, UserMessageNotificationDTO.class);
            sendNotificationUpdate(userId, dto);
        }

        List<UserDTO> assignees = new ArrayList<>();
        String subject = eventType + " Status Update Notification: " + title;

        for (String userId : taskEvent.getAssignees()) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            assignees.add(user);
        }

        for (UserDTO user : assignees) {
            try {
                HashMap<String, Object> variables = new HashMap<>();
                variables.put("userName", user.getName());
                variables.put("title", title);
                variables.put("description", description);
                variables.put("taskId", taskId);
                variables.put("oldStatus", oldStatus);
                variables.put("newStatus", newStatus);
                variables.put("eventType", eventType);
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

        String subject = "Task Deletion Notification: " + title;

        for (String userId : memberIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null && user.getEmail() != null) {
                Map<String, Object> variables = Map.of(
                        "userName", user.getName(),
                        "title", title,
                        "description", description != null ? description : "No description available",
                        "taskId", entityId
                );

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

        String subject = "New " + eventType + " Assignment: " + title;

        for (String userId : assigneeIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null && user.getEmail() != null) {
                Map<String, Object> variables = Map.of(
                        "userName", user.getName(),
                        "title", title,
                        "description", description != null ? description : "No description available",
                        "taskId", entityId,
                        "priority", priority,
                        "eventType", eventType
                );

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

        String subject = eventType + " Unassignment Notification: " + title;

        for (String userId : memberIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null && user.getEmail() != null) {
                Map<String, Object> variables = Map.of(
                        "userName", user.getName(),
                        "title", title,
                        "description", description != null ? description : "No description available",
                        "taskId", entityId,
                        "eventType", eventType
                );

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

        String subject = "Priority Updated Notification: " + title;

        for (String userId : memberIds) {
            UserDTO user = userFeignClient.getUserById(userId).getData();
            if (user != null && user.getEmail() != null) {
                Map<String, Object> variables = Map.of(
                        "userName", user.getName(),
                        "title", title,
                        "description", description != null ? description : "No description available",
                        "taskId", entityId,
                        "newPriority", newPriority
                );

                emailService.sendEmail(user.getEmail(), subject, "task-priority-update", variables);
                log.info("Email sent to {} for priority update (Task ID: {})", user.getEmail(), entityId);
            } else {
                log.warn("User with ID {} not found or has no email.", userId);
            }
        }
    }

    @Override
    public Publisher<UserMessageNotificationDTO> subscribeToNotification(String currentUserId) {

        // Fetch and map unread notifications
        List<UserMessageNotificationDTO> unreadDTOs = userMessageNotificationRepo
                .findAllByUserIdAndIsReadFalse(currentUserId).stream()
                .map(notification -> modelMapper.map(notification, UserMessageNotificationDTO.class))
                .toList();

        // Create or reuse the sink for this user
        Sinks.Many<UserMessageNotificationDTO> sink = userNotificationSinks.computeIfAbsent(
                currentUserId,
                key -> Sinks.many().multicast().directBestEffort()
        );
        // Return existing + future notifications as a Flux
        return Flux.concat(
                Flux.fromIterable(unreadDTOs),
                sink.asFlux()
        );
    }

    @Override
    public UserMessageNotificationDTO readSingleNotification(Long id) {
        UserMessageNotification umn = userMessageNotificationRepo.findById(id).orElse(null);
        if(umn != null) {
            userMessageNotificationRepo.deleteById(id);
        }
        return modelMapper.map(umn,UserMessageNotificationDTO.class);
    }

    @Override
    public List<UserMessageNotificationDTO> readAllNotification(String userId) {
        List<UserMessageNotification > umns = userMessageNotificationRepo.findAllByUserId(userId);
        userMessageNotificationRepo.deleteAll(umns);
        return umns.stream()
                .map(umn -> modelMapper.map(umn, UserMessageNotificationDTO.class))
                .toList();


    }

}
