package com.pms.userservice.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    // Topic name constant
    public static final String PASSWORD_RESET_TOPIC = "user.password-reset-requested";
    public static final String USER_SIGNUP_EVENT = "user-signup";
    /**
     * Kafka Topic bean for password reset events.
     * Creates the topic with 2 partitions and a replication factor of 1.
     */
    @Bean
    public NewTopic passwordResetRequestedTopic() {
        return new NewTopic(PASSWORD_RESET_TOPIC, 2, (short) 1);
    }

    @Bean
    public NewTopic userSignupRequestedTopic() {
        return new NewTopic(USER_SIGNUP_EVENT, 2, (short) 1);
    }
}
