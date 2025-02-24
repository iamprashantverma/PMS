package com.pms.TaskService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.schema.DataFetchingEnvironment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO extends IssueDTO{

    private boolean isBlocking;
    private List<String> memberId;

    private EpicDTO epic;
    private List<SubTaskDTO> subTasks;
}
