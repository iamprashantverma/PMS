package com.pms.Notification_Service.dto;

import com.pms.projectservice.event.enums.Actions;
import com.pms.projectservice.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import com.pms.projectservice.event.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMessageNotificationDTO {

    private Long notificationId;
    private String title;
    private String userId;
    private String entityId;
    private String description;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private LocalDate deadline;
    private Priority priority;
    private String projectId;
    private String assignedTo;
    private Status oldStatus;
    private Status newStatus;
    private String updatedBy;
    private LocalDateTime completionTime;
    private EventType eventType;
    private Actions action;
    private Boolean isReads;
    private String commentText;
    private String commentedBy;
}
