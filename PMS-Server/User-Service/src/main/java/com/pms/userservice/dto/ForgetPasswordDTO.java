package com.pms.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgetPasswordDTO {

    @Size(min = 6,max = 8 ,message = "please enter window id in the range")
    @NotBlank(message = "please provide window id for your current window")
    private  String windowId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;



}
