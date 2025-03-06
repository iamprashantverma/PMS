package com.pms.activitytrackingservice.dto;

import com.pms.activitytrackingservice.entities.enums.Actions;
import com.pms.activitytrackingservice.entities.enums.Status;
import jakarta.annotation.Priority;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CalendarDTO {

    private Long id;
    private String entityId;
    private LocalDate createDate;
    private LocalDateTime deadLine;
    private Long completionPercent;
    @ElementCollection
    private List<String> assignees;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private Actions action;


}
