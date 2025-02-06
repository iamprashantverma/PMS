package com.pms.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;


@Data
public class LoginRequestDTO {

    @NotNull(message = "please enter email")
    @Email(message = "please enter valid email address")
    private String email;

    @NotNull(message = "please enter your password")
    @NotBlank(message = "please enter valid password")
    private String password;

}
