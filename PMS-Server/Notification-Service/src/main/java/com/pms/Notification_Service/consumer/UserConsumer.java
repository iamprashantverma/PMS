package com.pms.Notification_Service.consumer;

import com.pms.Notification_Service.service.NotificationService;
import com.pms.Notification_Service.service.UserService;
import com.pms.userservice.events.PasswordResetRequestedEvent;
import com.pms.userservice.events.UserSignupEvent;
import com.pms.userservice.events.enums.EventType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for handling user-related events such as password resets,
 * signups, updates, and deactivations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserConsumer {

    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * Consumes password reset events and triggers email notifications.
     *
     * @param event the {@link PasswordResetRequestedEvent} containing reset details
     */
    @KafkaListener(topics = "user.password-reset-requested", groupId = "user.password-reset-requested-group")
    public void consumePasswordResetTopic(PasswordResetRequestedEvent event) throws MessagingException {
        log.info("Received Password Reset Event for user ID: {}", event.getId());
        notificationService.sendForgetPasswordOtp(event);
    }

    /**
     * Consumes user signup/update/deactivation events and routes them to the appropriate service method.
     *
     * @param event the {@link UserSignupEvent} containing user details and event type
     */
    @KafkaListener(topics = "user-signup", groupId = "user-signup-group")
    public void consumeUserSignUpTopic(UserSignupEvent event) {
        EventType eventType = event.getEventType();
        log.info("Received User Event [{}] for user ID: {}", eventType, event.getUserId());

        switch (eventType) {
            case USER_SIGNUP -> {
                log.info("Processing USER_SIGNUP event for user ID: {}", event.getUserId());
                userService.saveUserDetails(event);
            }
            case USER_UPDATED -> {
                log.info("Processing USER_UPDATED event for user ID: {}", event.getUserId());
                userService.updateUserDetails(event);
            }
            case USER_DEACTIVATED -> {
                log.info("Processing USER_DEACTIVATED event for user ID: {}", event.getUserId());
                userService.removeUserDetails(event);
            }
            default -> log.warn("Received unsupported event type: {} for user ID: {}", eventType, event.getUserId());
        }
    }
}
