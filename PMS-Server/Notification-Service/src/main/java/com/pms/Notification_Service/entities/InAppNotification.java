package com.pms.Notification_Service.entities;

import com.pms.projectservice.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import com.pms.projectservice.event.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
public class InAppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    private String userId;
    private String taskId;
    private String description;
    private LocalDate dueDate;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private Priority priority;
    private String projectId;
    private String assignedBy;
    private Set<String> assignees;
    private Status oldStatus;
    private Status newStatus;
    private String updatedBy;
    private LocalDateTime completionTime;
    private EventType eventType;

}
