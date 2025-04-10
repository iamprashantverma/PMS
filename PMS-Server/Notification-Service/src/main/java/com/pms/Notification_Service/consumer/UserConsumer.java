package com.pms.Notification_Service.consumer;

import com.pms.Notification_Service.service.NotificationService;
import com.pms.userservice.events.PasswordResetRequestedEvent;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user.password-reset-requested", groupId = "user.password-reset-requested-group")
    public void consumePasswordResetTopic(PasswordResetRequestedEvent passwordResetRequestedEvent) throws MessagingException {
        log.info("Received Password Reset Event: {}", passwordResetRequestedEvent);
        notificationService.sendForgetPasswordOtp(passwordResetRequestedEvent);
    }
}
