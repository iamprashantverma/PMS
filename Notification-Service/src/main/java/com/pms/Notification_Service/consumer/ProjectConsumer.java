package com.pms.Notification_Service.consumer;

import com.pms.projectservice.event.ProjectEvent;
import com.pms.Notification_Service.service.NotificationService;
import com.pms.Notification_Service.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectConsumer {

    private final NotificationService notificationService;


    @KafkaListener(topics = "project-topic", groupId = "project-group")
    public void consumeProjectEvents(ProjectEvent event) {
        log.info("Project Event successfully received: {}", event);



    }

}
