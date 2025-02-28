package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Epic entity.
 */
@Getter
@Setter
@Data
public class EpicDTO  {

    /**
     * The main goal or objective of this epic.
     */
    private String epicGoal;

    /**
     * List of stories linked to this epic.
     */
    private List<StoryDTO> stories;

    /**
     * List of tasks directly assigned under this epic.
     */
    private List<TaskDTO> tasks = new ArrayList<>();
    private String id;
    private String title;
    private String description;
    private String projectId;
    private List<String> assignees;
    private String creator;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Status status;
    private Priority priority;
    private Long completionPercent;
}
