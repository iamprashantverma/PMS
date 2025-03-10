package com.pms.activitytrackingservice.consumer;

import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import com.pms.activitytrackingservice.dto.CalendarDTO;
import com.pms.activitytrackingservice.services.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarConsumer {

    private final CalendarService calendarService;

    @KafkaListener(topics = "calendar-topic", groupId = "calendar-group")
    public void handleCalendarEvent(TaskEvent taskEvent) {
        log.info("Calendar event successfully received: {}", taskEvent);

        // Create a custom CalendarDTO from TaskEvent
        CalendarDTO calendarDTO = CalendarDTO.builder()
                .entityId(taskEvent.getEntityId())
                .projectId(taskEvent.getProjectId())
                .createDate(taskEvent.getCreatedDate())
                .deadLine(taskEvent.getDeadline())
                .completionPercent(0L)
                .assignees(taskEvent.getAssignees())
                .oldStatus(taskEvent.getOldStatus())
                .newStatus(taskEvent.getNewStatus())
                .build();

        // Handle different event actions
        if (taskEvent.getAction() == Actions.CREATED) {
            calendarService.createEvent(calendarDTO);
        } else if (taskEvent.getAction() == Actions.UPDATED) {
            calendarService.updateEvent(calendarDTO);
        } else {
            log.info("Unknown Action Found on the Calendar Event: {}", taskEvent.getAction());
        }
    }


}
