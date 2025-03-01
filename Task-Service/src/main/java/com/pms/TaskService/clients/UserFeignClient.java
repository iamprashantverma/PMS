package com.pms.TaskService.clients;

import com.pms.TaskService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service",path = "/user")
public interface UserFeignClient {

    @GetMapping("/details")
    UserDTO getUserById(@RequestParam("userId") String userId);
}
