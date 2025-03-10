package com.pms.TaskService.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.event.enums.Priority;
import com.pms.TaskService.event.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEvent {

    private String entityId;
    private String title;
    private String description;
    private String projectId;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updatedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private Priority priority;
    private Status oldStatus;
    private Status newStatus;
    private String assignedBy;
    private Set<String> assignees;
    private String updatedBy;
    private EventType eventType;
    private Actions action;

//    /* Extra Fields */
//    private Set<String> tags;
//    private String commentText;
//    private String commentedBy;
//    private List<String> attachmentUrls;
//    private Set<String> linkedEntityIds;

}
