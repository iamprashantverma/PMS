package com.pms.TaskService.event;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TaskEvent {
    private String title;
    private String taskId;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Priority priority;
    private String projectId;
    private String assignedBy;
    private List<String> assignees;
    private Status oldStatus;
    private Status newStatus;
    private String updatedBy;
    private LocalDateTime completionTime;
    private EventType eventType;
}
