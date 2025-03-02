package com.pms.Notification_Service.event;


import com.pms.Notification_Service.event.enums.Actions;
import com.pms.Notification_Service.event.enums.EventType;
import com.pms.Notification_Service.event.enums.Priority;
import com.pms.Notification_Service.event.enums.Status;
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
    private Actions action;
    private String assignedBy;
    private List<String> assignees;
    private Status oldStatus;
    private Status newStatus;
    private String updatedBy;
    private LocalDateTime completionTime;
    private EventType eventType;
}
