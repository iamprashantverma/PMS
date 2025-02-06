package com.pms.projectservice.entity;


import com.pms.projectservice.entity.enums.ProjectPriority;
import com.pms.projectservice.entity.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String title;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

    @Enumerated(EnumType.STRING)
    private ProjectPriority projectPriority;

    private String ownerId;
//    private String ownerName;

    private String clientId;

    private List<String> taskId;

    private List<String> milestoneId;

    private List<String> chatRoomId;

    private List<String> documentId;

    private List<String> updations;





}
