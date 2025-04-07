package com.pms.Notification_Service.resolver;

import com.pms.Notification_Service.dto.UserMessageNotificationDTO;
import com.pms.Notification_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class Notification {

    private final NotificationService notificationService;

    @SubscriptionMapping
    public Publisher<UserMessageNotificationDTO> getLatestNotification(@Argument("userId") String userId) {
        return notificationService.subscribeToNotification(userId);
    }
}
