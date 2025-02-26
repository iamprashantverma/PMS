package com.pms.Notification_Service.service;


import com.pms.Notification_Service.event.ProjectEvent;
import com.pms.Notification_Service.event.TaskEvent;

public interface NotificationService {

    void taskCreatedHandler(TaskEvent taskEvent);

    void taskUpdatedHandler(TaskEvent taskEvent);

    void taskCompletedHandler(TaskEvent taskEvent);

    void projectCreatedHandler(ProjectEvent projectEvent);

    void projectUpdatedHandler(ProjectEvent projectEvent);

    void projectCompletedHandler(ProjectEvent projectEvent);

    void priorityUpdatedHandler(ProjectEvent projectEvent);

    void memberAssignedHandler(ProjectEvent projectEvent);

    void memberRemovedHandler(ProjectEvent projectEvent);

    void statusUpdatedHandler(ProjectEvent projectEvent);

    void projectDeadlineExtendedHandler(ProjectEvent projectEvent);

}
