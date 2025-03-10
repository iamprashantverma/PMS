package com.pms.TaskService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "please provide the task id")
    @NotBlank(message = "please enter valid taskId")
    private String taskId;
}
