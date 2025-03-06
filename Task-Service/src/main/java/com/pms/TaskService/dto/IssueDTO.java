package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Issue entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueDTO {

    private String id;
    private String title;
    private String description;
    private String projectId;
    private List<String> assignees;
    private String creator;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private Status status;
    private Priority priority;
    private Long completionPercent;
    
}
