package com.pms.projectservice.event;

import com.pms.projectservice.event.enums.EventType;
import com.pms.projectservice.event.enums.Priority;
import com.pms.projectservice.event.enums.Status;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class ProjectEvent {

    private String projectId;
    private String name;
    private String description;
    private String triggeredBy;
    private Set<String> members;
    private String memberId;
    private Status oldStatus;
    private Status newStatus;
    private Priority oldPriority;
    private Priority newPriority;
    private EventType eventType;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private LocalDate timestamp;
    private LocalDate newDeadLine;
    private LocalDate oldDeadLine;

}