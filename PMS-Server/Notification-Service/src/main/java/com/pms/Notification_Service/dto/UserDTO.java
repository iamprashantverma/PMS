package com.pms.Notification_Service.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String userId;
    private String name;
    private String email;
    private String phoneNo;
    private String gender;
    private String language;
    private Boolean commentMentions;
    private Boolean taskUpdates;
    private Boolean bugUpdates;
    private Boolean emailUpdates;
    private Boolean subTaskUpdates;
}
