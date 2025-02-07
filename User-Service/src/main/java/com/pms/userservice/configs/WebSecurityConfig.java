package com.pms.userservice.configs;

import com.pms.userservice.filters.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {

//    private final JWTFilter jwtFilter;
//
//    /* public routes eg login , signup , password reset.. */
//    private static final String [] publicRoutes = {
//            "/auth/**"
//    };
//
//
//    /* Custom Bean of the Security Filter Chain */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return  http
//                    .cors(cors -> cors.configurationSource(request -> {
//                        var config = new org.springframework.web.cors.CorsConfiguration();
//                            config.setAllowedOrigins(List.of("http://localhost:3000"));
//                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                            config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "multipart/form-data"));
//                            config.setAllowCredentials(true);
//                            return config;
//                        }))
//
//                    .authorizeHttpRequests(req->req
//                        .requestMatchers("/auth/**").permitAll()
//                        .anyRequest().authenticated())
//                        .csrf(AbstractHttpConfigurer::disable)
//                        .sessionManagement(sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                    .build();
//
//    }


}
