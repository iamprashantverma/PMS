package com.pms.Notification_Service.service.impl;

import com.pms.Notification_Service.event.ProjectEvent;
import com.pms.Notification_Service.event.TaskEvent;
import com.pms.Notification_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl  implements NotificationService {

    private final EmailService emailService;

    @Override
    public void taskCreatedHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskUpdatedHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskCompletedHandler(TaskEvent taskEvent) {

    }

    @Override
    public void projectCreatedHandler(ProjectEvent projectEvent) {

    }

    @Override
    public void projectUpdatedHandler(ProjectEvent projectEvent) {

    }

    @Override
    public void projectCompletedHandler(ProjectEvent projectEvent) {

    }

    @Override
    public void priorityUpdatedHandler(ProjectEvent projectEvent) {

    }

    @Override
    public void memberAssignedHandler(ProjectEvent projectEvent) {

    }

    @Override
    public void memberRemovedHandler(ProjectEvent projectEvent) {

    }

    @Override
    public void statusUpdatedHandler(ProjectEvent projectEvent) {

    }

    @Override
    public void projectDeadlineExtendedHandler(ProjectEvent projectEvent) {

    }
}
