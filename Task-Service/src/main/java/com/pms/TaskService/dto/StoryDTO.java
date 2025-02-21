package com.pms.TaskService.dto;

import com.pms.TaskService.entities.Epic;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class StoryDTO extends IssueDTO {
    private String acceptanceCriteria;
    private EpicDTO epic;

}
