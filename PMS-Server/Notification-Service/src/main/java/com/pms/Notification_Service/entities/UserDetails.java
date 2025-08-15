package com.pms.Notification_Service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserDetails {

    @Id
    private String userId;
    private String email;
    private Boolean commentMentions;
    private Boolean taskUpdates;
    private Boolean subTaskUpdates;
    private Boolean bugUpdates;
    private Boolean emailUpdates;

}
