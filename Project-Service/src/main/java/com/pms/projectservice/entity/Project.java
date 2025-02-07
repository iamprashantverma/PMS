package com.pms.projectservice.entity;


import com.pms.projectservice.entity.enums.Priority;
import com.pms.projectservice.entity.enums.Status;
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
    private String projectId;

    private String title;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime deadline;
    private LocalDateTime extendedDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String ownerId;
//    private String ownerName;

    private String clientId;

    private List<String> memberId;
    private List<String> taskId;

    private List<String> milestoneId;

    private List<String> chatRoomId;

    private List<String> documentId;

    private List<String> updations;





}
