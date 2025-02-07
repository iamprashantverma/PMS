package com.pms.userservice.dto;

import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    
    private String userId;

    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Please enter the name")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Please enter the email")
    private String email;

    private String image;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    @NotNull(message = "Please enter the phone number")
    private String phoneNo;

    @NotNull(message = "Role is required")
    private Roles role;

    private String gender;

    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Please enter your DOB")
    private LocalDate dob;

    private Status status;
    private String password;

    private LocalDateTime lastLoginTime;

    private String address;

    private List<String> projectId;

    private List<String> taskId;

    private String language;

    private LocalDateTime creationDate;
}
