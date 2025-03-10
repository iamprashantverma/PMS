package com.pms.Notification_Service.clients;

import com.pms.Notification_Service.advices.APIResponse;
import com.pms.Notification_Service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-SERVICE", path = "/users")
public interface UserFeignClient {

    @GetMapping("/details")
    APIResponse<UserDTO> getUserById(@RequestParam("userId") String userId);

}
