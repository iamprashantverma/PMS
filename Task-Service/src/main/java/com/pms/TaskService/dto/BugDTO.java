package com.pms.TaskService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Bug entity, extending IssueDTO to inherit common issue properties.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BugDTO extends IssueDTO {

    /**
     * Steps to reproduce the bug.
     */
    private String stepsToReproduce;

    /**
     * Expected behavior after executing the steps.
     */
    private String expectedOutcome;
    private LocalDate deadline;

    /**
     * Actual behavior that differs from the expected outcome.
     */
    private String actualOutcome;
    private String epicId;
    private String taskId;
    private String storyId;

}
