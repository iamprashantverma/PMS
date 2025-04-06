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

    @ElementCollection
    @CollectionTable(name = "user_projects", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> projectIds;

    @ElementCollection
    @CollectionTable(name = "user_tasks", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> taskIds;

    @ElementCollection
    @CollectionTable(name = "sub_tasks", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> SubTaskIds;

    @ElementCollection
    @CollectionTable(name = "user_epics", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> epicIds;

    @ElementCollection
    @CollectionTable(name = "user_bugs", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> bugIds;

    private String language;

    @CreationTimestamp
    private LocalDate joinedAt;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Session> sessions;


}
