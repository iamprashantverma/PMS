package com.pms.userservice.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignupEvent {
    private String userId;
    private String email;
    private Boolean commentMentions;
    private Boolean taskUpdates;
    private Boolean subTaskUpdates;
    private Boolean bugUpdates;
    private Boolean emailUpdates;
}
