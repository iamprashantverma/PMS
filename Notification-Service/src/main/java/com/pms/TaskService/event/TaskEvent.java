package com.pms.TaskService.event;


import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import com.pms.projectservice.event.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class TaskEvent {

    /* Unique identifier for task, bug, epic, story, subtask, etc. */
    private String entityId;


    /* Title of the task, bug, epic, story, or subtask */
    private String title;

    /* Description of the task, bug, epic, story, or subtask */
    private String description;

    /* Project ID to which this entity belongs */
    private String projectId;

    /* Timestamp when the entity was created */
    private LocalDate createdDate;

    /* Timestamp when the entity was last updated */
    private LocalDate updatedDate;

    /* Due date for the task, bug, epic, or subtask */
    private LocalDate deadline;

    /* Timestamp when the entity was completed */
    private LocalDateTime completionTime;

    /* Priority level: High, Medium, Low */
    private Priority priority;

    /* Previous status before an update */
    private Status oldStatus;

    /* New status after an update */
    private Status newStatus;

    /* User who assigned this task, bug, or story */
    private String assignedBy;

    /* List of users assigned to this entity */
    private Set<String> assignees;

    /* User who performed the last update */
    private String updatedBy;


    private EventType eventType;

    /* Action performed on this entity (CREATED, UPDATED, DELETED) */
    private Actions action;

    /* Tags associated with the entity (for tagging feature) */
    private Set<String> tags;

    /* Comment text, used when a comment is added */
    private String commentText;

    /* User who added the comment */
    private String commentedBy;

    /* List of attachment URLs (for files/images) */
    private Set<String> attachmentUrls;

    /* List of linked entities (used for linking tasks, bugs, epics, etc.) */
    private Set<String> linkedEntityIds;
}
