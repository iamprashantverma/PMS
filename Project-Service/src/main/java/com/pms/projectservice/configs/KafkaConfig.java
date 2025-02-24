package com.pms.projectservice.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    public static String KAFKA_PROJECT_SERVICE_TOPIC = "project-topic";

    @Bean
    public NewTopic getNewTopic(){
        return  new NewTopic(KAFKA_PROJECT_SERVICE_TOPIC,2, (short)1);
    }
}

