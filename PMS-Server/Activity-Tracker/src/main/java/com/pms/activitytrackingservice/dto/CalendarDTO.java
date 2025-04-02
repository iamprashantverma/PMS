package com.pms.activitytrackingservice.dto;

import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.event.enums.Priority;
import com.pms.TaskService.event.enums.Status;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDTO {

    private Long id;
    private String entityId;
    private String projectId;
    private LocalDate createDate;
    private LocalDate deadLine;
    private Long completionPercent;
    private String title;
    private EventType eventType;

    @ElementCollection
    private Set<String> assignees;

    @Enumerated(EnumType.STRING)
    private Status oldStatus;

    @Enumerated(EnumType.STRING)
    private Status newStatus;

    @Enumerated(EnumType.STRING)
    private Priority priority;
    private EventType event;
}
