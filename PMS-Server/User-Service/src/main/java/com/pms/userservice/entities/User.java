package com.pms.userservice.entities;

import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    private static  final String PREFIX = "DEV-";
    @Id
    private String userId;

    @PrePersist
    private void generateUserId() {
        this.userId = PREFIX + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String name;
    private String password;
    private String email;
    private String image;
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private Roles role;

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

    private Boolean commentMentions;
    private Boolean taskUpdates;
    private Boolean bugUpdates;
    private Boolean emailUpdates;
    private Boolean subTaskUpdates;

}
