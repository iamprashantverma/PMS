package com.pms.Notification_Service.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String userId;
    private String name;
    private String email;
    private String phoneNo;
//    private Roles role;
    private String gender;
//    private Status status;
    private String password;
    private String language;
}
