package com.pms.Notification_Service.service;

import com.pms.Notification_Service.clients.UserFeignClient;
import com.pms.Notification_Service.event.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final UserFeignClient userFeignClient;

    /* Handle task created event */
    public void taskCreatedHandler(TaskEvent event) {
//        try {
//            log.info("Processing TASK_CREATED event for Task ID: {}", event.getTaskId());
//
//
//
//            log.info("Email successfully sent for Task ID: {}", event.getTaskId());
//
//        } catch (Exception e) {
//            log.error("Failed to send email for TASK_CREATED event, Task ID: {}", event.getTaskId(), e);
//            // Retry mechanism or Dead Letter Queue (DLQ) can be implemented here
//        }
    }

    /* Handle task updated event */
    public void taskUpdatedHandler(TaskEvent event) {
//        try {
//            log.info("Processing TASK_UPDATED event for Task ID: {}", event.getTaskId());
//            /* send the email to all the users related to this event */
//            for (String userId : event.getAssignees()) {
//
//                /* fetch the user from the User Service */
//                UserDTO user = userFeignClient.getUserById(userId).getData();
//
//                String subject = "Task Updated: " + event.getTaskId();
//                String message = "Task has been updated.\n\n" +
//                        "Task ID: " + event.getTaskId() + "\n" +
//                        "Updated By: " + event.getUpdatedBy() + "\n" +
//                        "New Status: " + event.getOldStatus();
//
//                emailService.sendMail(user.getUserId(), subject, message);
//            }
//            log.info("Email sent for Task Updated: {}", event.getTaskId());
//
//        } catch (Exception e) {
//            log.error("Failed to send email for TASK_UPDATED event, Task ID: {}", event.getTaskId(), e);
//        }
    }

    /* Handle task completed event */
    public void taskCompletedHandler(TaskEvent event) {
//        try {
//            log.info("Processing TASK_COMPLETED event for Task ID: {}", event.getTaskId());
//
//            String subject = "Task Completed: " + event.getTaskId();
//            String message = "The task has been successfully completed.\n\n" +
//                    "Task ID: " + event.getTaskId() + "\n" +
//                    "Completed By: " + eve;
//
//            emailService.sendMail(event, subject, message);
//            log.info("Email sent for Task Completed: {}", event.getTaskId());
//
//        } catch (Exception e) {
//            log.error("Failed to send email for TASK_COMPLETED event, Task ID: {}", event.getTaskId(), e);
//        }
    }


}
