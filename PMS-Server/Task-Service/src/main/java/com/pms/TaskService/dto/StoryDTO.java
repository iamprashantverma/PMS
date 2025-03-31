package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Story entity, extending IssueDTO to inherit common issue properties.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryDTO extends IssueDTO {

    /**
     * Defines conditions that must be met for the story to be considered complete.
     */
    private String acceptanceCriteria;

    /**
     * ID of the epic this story belongs to, if any.
     */
    private String epicId;
    private String id;
    private String title;
    private String description;
    private String projectId;
    private List<String> assignees;
    private String creator;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Status status;
    private Priority priority;
    private Long completionPercent;
}
