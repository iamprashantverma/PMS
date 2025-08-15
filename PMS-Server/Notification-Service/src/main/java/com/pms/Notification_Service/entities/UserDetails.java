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
    private String name;
    private Boolean commentMentions;
    private Boolean taskUpdates;
    private Boolean subTaskUpdates;
    private Boolean bugUpdates;
    private Boolean emailUpdates;
    @Override
    public String toString() {
        return "UserDetails{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", commentMentions=" + commentMentions +
                ", taskUpdates=" + taskUpdates +
                ", subTaskUpdates=" + subTaskUpdates +
                ", bugUpdates=" + bugUpdates +
                ", emailUpdates=" + emailUpdates +
                '}';
    }


}
