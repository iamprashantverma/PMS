package com.pms.TaskService.clients;

import com.pms.TaskService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for interacting with the User Service.
 */
@FeignClient(name = "user-service", path = "/users")
public interface UserFeignClient {

    /**
     * Retrieves user details by user ID.
     *
     * @param userId the ID of the user
     * @return the UserDTO containing user details
     */
    @GetMapping("/details")
    UserDTO getUserById(@RequestParam("userId") String userId);
}
