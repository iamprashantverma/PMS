package com.pms.projectservice.entities;

import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    private static final String PREFIX = "PROJ-";

    @Id
    private String projectId;

    @PrePersist
    public void createId() {

        this.projectId = PREFIX + UUID.randomUUID().toString().substring(0, 8);
    }

    private String title;
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate deadline;

    private LocalDate endDate;

    @CreationTimestamp
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(nullable = false)
    private String projectCreator;

    private String clientId;

    @ElementCollection
    private Set<String> memberIds = new HashSet<>();

    @ElementCollection
    private Set<String> epicIds =  new HashSet<>();

    @ElementCollection
    private Set<String> taskIds =  new HashSet<>();

    @ElementCollection
    private Set<String> bugIds =  new HashSet<>();

    @ElementCollection
    private Set<String> chatRoomsIds = new HashSet<>();


}
