package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.Priority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectDTO {
    private String projectId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate deadline;
    private LocalDate extendedDate;
    private Priority priority;
    private String ownerId;

}
