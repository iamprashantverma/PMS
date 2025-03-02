package com.pms.Notification_Service.service.impl;


import com.pms.Notification_Service.event.TaskEvent;
import com.pms.Notification_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl  implements NotificationService {


    @Override
    public void taskTopicCreationHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskTopicUpdateHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskTopicStatusUpdateHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskTopicDeletionHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskTopicMemberAssignedHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskTopicMemberUnassignedHandler(TaskEvent taskEvent) {

    }

    @Override
    public void taskTopicPriorityUpdatedHandler(TaskEvent taskEvent) {

    }



}
