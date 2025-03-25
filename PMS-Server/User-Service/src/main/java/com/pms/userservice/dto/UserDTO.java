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

    @NotNull(message = "Please enter the phone number")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be 10 to 15 digits and can optionally start with +"
    )
    private String phoneNo;

    @NotNull(message = "Role is required")
    private Roles role;

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Please enter your DOB")
    private LocalDate dob;

    private Status status;
    private String password;

    private LocalDateTime lastLoginTime;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    @NotBlank(message = "Language is required")
    @Size(max = 50, message = "Language must be less than 50 characters")
    private String language;

    private LocalDate joinedAt;
}
