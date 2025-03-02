package com.pms.Notification_Service.service;


import com.pms.Notification_Service.event.ProjectEvent;
import com.pms.Notification_Service.event.TaskEvent;

public interface NotificationService {


    void taskTopicCreationHandler(TaskEvent taskEvent);

    void taskTopicUpdateHandler(TaskEvent taskEvent);

    void taskTopicStatusUpdateHandler(TaskEvent taskEvent);

    void taskTopicDeletionHandler(TaskEvent taskEvent);

    void taskTopicMemberAssignedHandler(TaskEvent taskEvent);

    void taskTopicMemberUnassignedHandler(TaskEvent taskEvent);

    void taskTopicPriorityUpdatedHandler(TaskEvent taskEvent);
}
