package com.pms.userservice.controller;

import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /*  get User By their id */
    @GetMapping("/details")
    public ResponseEntity<UserDTO> getUserById(@RequestParam("userId") String userId ) {
        UserDTO userDTO = userService.getUserDetails(userId);
        return ResponseEntity.ok(userDTO);
    }


}
