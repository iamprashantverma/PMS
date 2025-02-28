package com.pms.TaskService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for SubTask entity, extending IssueDTO to inherit common issue properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubTaskDTO extends IssueDTO {

    /**
     * ID of the parent task this subtask belongs to.
     */
    private String parentTaskId;
}
