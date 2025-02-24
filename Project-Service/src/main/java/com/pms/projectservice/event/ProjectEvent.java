package com.pms.projectservice.event;

import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectEvent {
    private String projectId;
    private String name;
    private String description;
    private String triggeredBy;
    private List<String> members;
    private String memberId;
    private Status oldStatus;
    private Status newStatus;
    private Priority oldPriority;
    private Priority newPriority;
    private EventType eventType;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime timestamp;
    private LocalDateTime newDeadLine;
    private LocalDateTime oldDeadLine;
}