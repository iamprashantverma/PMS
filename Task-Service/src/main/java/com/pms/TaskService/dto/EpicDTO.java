package com.pms.TaskService.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object for Epic entity.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Data
public class EpicDTO extends IssueDTO {

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
    private List<TaskDTO> tasks;
}
