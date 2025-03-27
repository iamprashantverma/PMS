package com.pms.userservice.controller;

import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    /* update the user details*/
    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateUserDetails(@RequestPart("userData") @Valid UserDTO userDTO, @RequestParam("image")MultipartFile file) {

        ResponseDTO responseDTO = userService.updateUserDetails(userDTO,file);
        return ResponseEntity.ok(responseDTO);
    }


}
