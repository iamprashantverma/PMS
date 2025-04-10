package com.pms.userservice.producer;

import com.pms.userservice.events.PasswordResetRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.pms.userservice.configs.KafkaConfig.PASSWORD_RESET_TOPIC;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<String, PasswordResetRequestedEvent> kafkaTemplate;

    public void sendPasswordResetRequestedEvent(PasswordResetRequestedEvent passwordResetRequestedEvent) {
        kafkaTemplate.send(PASSWORD_RESET_TOPIC,passwordResetRequestedEvent.getUserId(),passwordResetRequestedEvent);
    }
}
