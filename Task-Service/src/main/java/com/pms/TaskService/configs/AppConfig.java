package com.pms.TaskService.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /* Creating the bean so that IOC handle the DI, lifeCycles */
    @Bean
    public ModelMapper getModelMapper() {
        return  new ModelMapper();
    }



}
