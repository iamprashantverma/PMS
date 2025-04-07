package com.pms.Notification_Service.service;

import com.pms.Notification_Service.dto.UserMessageNotificationDTO;
import com.pms.TaskService.event.TaskEvent;
import jakarta.mail.MessagingException;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.concurrent.Flow;

public interface NotificationService {

    void taskTopicUpdateHandler(TaskEvent taskEvent) throws MessagingException;

    void taskTopicStatusUpdateHandler(TaskEvent taskEvent);

    void taskTopicDeletionHandler(TaskEvent taskEvent) throws MessagingException;

    void taskTopicMemberAssignedHandler(TaskEvent taskEvent) throws MessagingException;

    void taskTopicMemberUnassignedHandler(TaskEvent taskEvent) throws MessagingException;

    void taskTopicPriorityUpdatedHandler(TaskEvent taskEvent) throws MessagingException;

    Publisher<UserMessageNotificationDTO> subscribeToNotification(String currentUserId);
}
