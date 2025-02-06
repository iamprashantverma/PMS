package com.pms.userservice.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    /* Creating the bean so that IOC handle the DI, lifeCycles */
    @Bean
    public ModelMapper getModelMapper() {
        return  new ModelMapper();
    }

    /* creating the bean of Password Encoder to code and decode sensitive info */
    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
