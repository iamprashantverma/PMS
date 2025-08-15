package com.pms.userservice.producer;

import com.pms.userservice.events.PasswordResetRequestedEvent;
import com.pms.userservice.events.UserSignupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.pms.userservice.configs.KafkaConfig.PASSWORD_RESET_TOPIC;
import static com.pms.userservice.configs.KafkaConfig.USER_SIGNUP_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<Long, PasswordResetRequestedEvent> resetRequestedEventKafkaTemplate;
    private final KafkaTemplate<String, UserSignupEvent>signupEventKafkaTemplate;

    public void sendPasswordResetRequestedEvent(PasswordResetRequestedEvent passwordResetRequestedEvent) {
        resetRequestedEventKafkaTemplate.send(PASSWORD_RESET_TOPIC,passwordResetRequestedEvent.getId(),passwordResetRequestedEvent);
        log.info("password-reset-event-send{}", passwordResetRequestedEvent.getEmail());
    }

    public void sendUserSignupEvent(UserSignupEvent user) {
        signupEventKafkaTemplate.send(USER_SIGNUP_EVENT,user.getUserId(),user);
    }
}
