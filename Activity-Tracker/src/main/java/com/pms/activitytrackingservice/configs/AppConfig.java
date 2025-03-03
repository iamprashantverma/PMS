package com.pms.activitytrackingservice.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /**
     * creating the bean of the ModelMapper
     * @return ModelMapper object of the modelmapper
     */
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

}
