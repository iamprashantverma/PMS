package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object (DTO) representing a Bug entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugDTO {

    /**
     * Steps to reproduce the bug.
     */
    private String stepsToReproduce;

    /**
     * Expected behavior after executing the steps.
     */
    private String expectedOutcome;

    /**
     * The deadline by which the bug should be fixed.
     */
    private LocalDate deadline;

    /**
     * Actual behavior that differs from the expected outcome.
     */
    private String actualOutcome;

    /**
     * The ID of the epic this bug is associated with.
     */
    private String epicId;

    /**
     * The ID of the task this bug is associated with.
     */
    private String taskId;

    /**
     * The ID of the story this bug is related to.
     */
    private String storyId;

    /**
     * Unique identifier for the bug.
     */
    private String id;

    /**
     * Title or short description of the bug.
     */
    private String title;

    /**
     * Detailed description of the bug.
     */
    private String description;

    /**
     * The project ID to which this bug belongs.
     */
    private String projectId;

    /**
     * List of users assigned to fix this bug.
     */
    private Set<String> assignees;

    /**
     * The creator of the bug.
     */
    private String creator;

    /**
     * The date when the bug was reported.
     */
    private LocalDate createdAt;

    /**
     * The last updated date of the bug.
     */
    private LocalDate updatedAt;

    /**
     * Current status of the bug.
     */
    private Status status;

    /**
     * Priority level of the bug.
     */
    private Priority priority;

    /**
     * Percentage of completion of the bug fix.
     */
    private Integer completionPercent;
    /**
     * Label of the BUG
     */
    private String label;

    private String image;

}
