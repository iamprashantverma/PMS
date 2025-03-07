package com.pms.Notification_Service.service;

import com.pms.TaskService.event.TaskEvent;

/**
 * Interface for handling various task-related events received from Kafka topics.
 */
public interface NotificationService {

    /**
     * Handles the event when a task is updated.
     *
     * @param taskEvent The event containing task update details.
     */
    void taskTopicUpdateHandler(TaskEvent taskEvent);

    /**
     * Handles the event when a task's status is updated.
     *
     * @param taskEvent The event containing task status update details.
     */
    void taskTopicStatusUpdateHandler(TaskEvent taskEvent);

    /**
     * Handles the event when a task is deleted.
     *
     * @param taskEvent The event containing task deletion details.
     */
    void taskTopicDeletionHandler(TaskEvent taskEvent);

    /**
     * Handles the event when a member is assigned to a task.
     *
     * @param taskEvent The event containing task member assignment details.
     */
    void taskTopicMemberAssignedHandler(TaskEvent taskEvent);

    /**
     * Handles the event when a member is unassigned from a task.
     *
     * @param taskEvent The event containing task member unassignment details.
     */
    void taskTopicMemberUnassignedHandler(TaskEvent taskEvent);

    /**
     * Handles the event when a task's priority is updated.
     *
     * @param taskEvent The event containing task priority update details.
     */
    void taskTopicPriorityUpdatedHandler(TaskEvent taskEvent);
}
