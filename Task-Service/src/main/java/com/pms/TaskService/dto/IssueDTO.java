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
public class IssueDTO {
    private String id;
    private String title;
    private String description;
    private String project;
    private List<String> assignees;
    private String creater;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Status status;  // Status (e.g., TODO, IN_PROGRESS, DONE)
    private Priority priority;
    private Long completionPercent;
    private IssueTag tag;

}
