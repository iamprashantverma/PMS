package com.pms.TaskService.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service",path = "/user")
public interface UserFeignClient {

}
