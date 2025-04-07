package com.pms.TaskService.auth;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;


@Component
public class FeinClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String userId = UserContextHolder.getCurrentUserId();
        if (userId != null) {
            requestTemplate.header("X-User-Id", userId);
        }
    }




}


