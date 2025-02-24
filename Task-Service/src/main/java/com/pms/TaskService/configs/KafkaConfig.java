package com.pms.TaskService.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    public static String KAFKA_TASK_SERVICE_TOPIC = "task-topic";
    @Bean
    public NewTopic getNewTopic(){
        return  new NewTopic(KAFKA_TASK_SERVICE_TOPIC,2, (short)2);
    }
}

