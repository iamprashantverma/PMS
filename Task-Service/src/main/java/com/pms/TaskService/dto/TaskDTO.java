package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.IssueTag;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private String title;
    private String description;
    private String projectId;
    private String createrId;  //creater
//    private LocalDateTime createdDate;
    private Status status;
    private Priority priority;
    private IssueTag tag; // frontend or backend
    private Long completionPercent;
    private List<String> memberId;

}
