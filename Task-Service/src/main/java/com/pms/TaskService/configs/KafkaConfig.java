package com.pms.TaskService.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    public static final String KAFKA_TASK_SERVICE_TOPIC = "task-topic";
    public static final String KAFKA_NOTIFICATION_SERVICE_TOPIC = "notification-topic";

    @Bean
    public NewTopic taskTopic() {
        return new NewTopic(KAFKA_TASK_SERVICE_TOPIC, 2, (short) 1);
    }

    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic(KAFKA_NOTIFICATION_SERVICE_TOPIC, 2, (short) 1);
    }
}
