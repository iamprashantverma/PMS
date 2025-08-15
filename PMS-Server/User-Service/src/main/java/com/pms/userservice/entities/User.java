package com.pms.userservice.entities;

import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    private static final String PREFIX = "DEV-";

    @Id
    private String userId;

    @PrePersist
    private void prePersist() {
        if (this.userId == null) {
            this.userId = PREFIX + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }
        // Ensure boolean fields default to true if not explicitly set
        if (commentMentions == null) commentMentions = true;
        if (taskUpdates == null) taskUpdates = true;
        if (bugUpdates == null) bugUpdates = true;
        if (emailUpdates == null) emailUpdates = true;
        if (subTaskUpdates == null) subTaskUpdates = true;
    }

    private String name;
    private String password;
    private String email;
    private String image;
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    private LocalDate dob;
    private String address;
    private String gender;
    private String language;

    @CreationTimestamp
    private LocalDate joinedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Session> sessions;

    private Boolean commentMentions = true;
    private Boolean taskUpdates = true;
    private Boolean bugUpdates = true;
    private Boolean emailUpdates = true;
    private Boolean subTaskUpdates = true;
}
