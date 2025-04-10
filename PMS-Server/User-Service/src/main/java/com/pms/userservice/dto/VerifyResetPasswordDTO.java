package com.pms.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyResetPasswordDTO {

    @NotBlank(message = "Reset token is required")
    private String windowId;

    @NotBlank(message = "please enter otp")
    private  String otp;

    @Email(message = "please enter valid email")
    private String email;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

}
