package com.pms.userservice.events;

import com.pms.userservice.events.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupEvent {
    private String userId;
    private String email;
    private String name;
    private Boolean commentMentions;
    private Boolean taskUpdates;
    private Boolean subTaskUpdates;
    private Boolean bugUpdates;
    private Boolean emailUpdates;
    private EventType eventType;
}
