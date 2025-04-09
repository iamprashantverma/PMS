package com.pms.activitytrackingservice.entities;

import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.event.enums.Priority;
import com.pms.TaskService.event.enums.Status;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityId;
    private String projectId;
    private LocalDate createDate;
    private LocalDate deadLine;
    private Long completionPercent;

    @ElementCollection
    private List<String> assignees;
    @Enumerated(EnumType.STRING)
    private Status newStatus;

    @Enumerated(EnumType.STRING)
    private Status oldStatus;

    @Enumerated(EnumType.STRING)
    private Priority priority;
    private  String title;
    private EventType eventType;

}
