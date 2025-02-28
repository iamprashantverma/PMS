package com.pms.TaskService.dto;

import lombok.*;

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
}
