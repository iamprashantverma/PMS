package com.pms.projectservice.entities;

import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
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

    private String projectCreator;
    private String clientId;

    @ElementCollection
    private List<String> membersId = new ArrayList<>();

    @ElementCollection
    private List<String> taskId = new ArrayList<>();

    @ElementCollection
    private List<String> milestoneId = new ArrayList<>();

    @ElementCollection
    private List<String> chatRoomId = new ArrayList<>();

    @ElementCollection
    private List<String> documentId = new ArrayList<>();
}
