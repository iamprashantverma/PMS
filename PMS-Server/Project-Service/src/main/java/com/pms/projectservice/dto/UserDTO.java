package com.pms.projectservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    
    private String userId;

    private String name;

    private String email;

    private String image;

    private String phoneNo;

    private String gender;

    private LocalDate dob;

    private LocalDateTime lastLoginTime;

    private String address;


    private String language;

    private LocalDateTime creationDate;
}
