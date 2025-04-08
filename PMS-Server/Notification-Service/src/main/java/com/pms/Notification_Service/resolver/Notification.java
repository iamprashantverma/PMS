package com.pms.Notification_Service.resolver;

import com.pms.Notification_Service.dto.UserMessageNotificationDTO;
import com.pms.Notification_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@RequiredArgsConstructor
@Slf4j
public class Notification {

    private final NotificationService notificationService;

    @SubscriptionMapping
    public Publisher<UserMessageNotificationDTO> getLatestNotification(@Argument("userId") String userId) {
        return notificationService.subscribeToNotification(userId);
    }

    @MutationMapping
    public UserMessageNotificationDTO readSingleNotification(@Argument("id")Long id) {
            return notificationService.readSingleNotification(id);

    }
    @MutationMapping
    public List<UserMessageNotificationDTO> readAllNotification(@Argument("userId")String userId) {
        return notificationService.readAllNotification(userId);
    }
}
