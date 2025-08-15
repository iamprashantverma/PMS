package com.pms.userservice.events;

import lombok.Data;

@Data
public class UserSignupEvent {
    private String userId;
    private String email;
    private Boolean commentMentions;
    private Boolean taskUpdates;
    private Boolean subTaskUpdates;
    private Boolean bugUpdates;
    private Boolean emailUpdates;
}
