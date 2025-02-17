package com.pms.projectservice.entities;

import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    private String projectId;

    private String title;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime deadline;
    private LocalDateTime endDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime extendedDate;

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
