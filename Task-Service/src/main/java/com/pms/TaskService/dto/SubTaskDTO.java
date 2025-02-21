package com.pms.TaskService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubTaskDTO extends IssueDTO {


    private TaskDTO parentTask;
}
