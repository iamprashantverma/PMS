package com.pms.projectservice.clients;

import com.pms.projectservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service",path = "/users")
public interface UserFeignClient {

    @GetMapping("/details")
    UserDTO getUserById(@RequestParam("userId") String userId);

}

