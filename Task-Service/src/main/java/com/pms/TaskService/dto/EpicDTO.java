package com.pms.TaskService.dto;


import com.pms.TaskService.entities.Task;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EpicDTO extends IssueDTO{
    private String epicGoal;

    private List<StoryDTO> stories;
    private List<TaskDTO> tasks;
}
