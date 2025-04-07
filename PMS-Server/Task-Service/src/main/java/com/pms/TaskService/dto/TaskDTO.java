package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for Task entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private String id;
    private String title;
    private String description;
    private String projectId;
    private List<String> assignees = new ArrayList<>();
    private String creator;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private LocalDate deadline;
    private Status status;
    private Priority priority;
    private Long completionPercent;
    private String epicId;
    private String storyId;
    private String label;
    private String image;
    private String reporter;
    private List<SubTaskDTO> subTasks = new ArrayList<>();
}
