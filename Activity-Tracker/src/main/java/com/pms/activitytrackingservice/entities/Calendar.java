package com.pms.activitytrackingservice.entities;

import com.pms.activitytrackingservice.entities.enums.Priority;
import com.pms.activitytrackingservice.entities.enums.Status;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityId;
    private LocalDate createDate;
    private LocalDateTime deadLine;
    private Long completionPercent;
    @ElementCollection
    private List<String> assignees;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Priority priority;

}
