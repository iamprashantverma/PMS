package com.pms.Notification_Service.entities;

import com.pms.projectservice.event.enums.Actions;
import com.pms.projectservice.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import com.pms.projectservice.event.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
public class UserMessageNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
    private Status oldStatus;
    private Status newStatus;
    private String updatedBy;
    private LocalDateTime completionTime;
    private EventType eventType;
    private Actions action;
    private Boolean isRead = false;
    private String commentText;
    private String commentedBy;
}
