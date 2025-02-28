package com.pms.TaskService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Task entity, extending IssueDTO to inherit common issue properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskDTO extends IssueDTO {

    /**
     * If true, this task is preventing others from progressing.
     */
    private boolean isBlocking;

    /**
     * ID of the epic this task belongs to, if any.
     */
    private String epicId;

    /**
     * ID of the story this task belongs to, if any.
     */
    private String storyId;

    /**
     * List of IDs of subtasks associated with this task.
     */
    private List<String> subTaskIds;
}
