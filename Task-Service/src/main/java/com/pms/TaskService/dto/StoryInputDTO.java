package com.pms.TaskService.dto;


import com.pms.TaskService.entities.Epic;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryInputDTO extends IssueInputDTO{
    private String acceptanceCriteria;

    private Epic epic;
}
