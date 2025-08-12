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
public class    CalendarConsumer {

    private final CalendarService calendarService;

    @KafkaListener(topics = "calendar-topic", groupId = "calendar-group")
    public void handleCalendarEvent(TaskEvent taskEvent) {

        log.info("Calendar event successfully received: {}", taskEvent);
        System.out.println(taskEvent.getEventType());
        // Create a custom CalendarDTO from TaskEvent
        CalendarDTO calendarDTO = CalendarDTO.builder()
                .entityId(taskEvent.getEntityId())
                .projectId(taskEvent.getProjectId())
                .createDate(taskEvent.getCreatedDate())
                .deadLine(taskEvent.getDeadline())
                .createDate(taskEvent.getCreatedDate())
                .completionPercent(taskEvent.getCompletionPercent())
                .assignees(taskEvent.getAssignees())
                .oldStatus(taskEvent.getOldStatus())
                .newStatus(taskEvent.getNewStatus())
                .title(taskEvent.getTitle())
                .eventType(taskEvent.getEventType())
                .priority(taskEvent.getPriority())
                .build();

        // Handle different event actions
        if (taskEvent.getAction() == Actions.CREATED) {
            calendarService.createEvent(calendarDTO);
        } else if (taskEvent.getAction() == Actions.UPDATED) {
            calendarService.updateEvent(calendarDTO);
        } else if(taskEvent.getAction() == Actions.STATUS_CHANGED)  {
            log.info("completion time is{}",taskEvent.getCompletionPercent());
            calendarService.statusUpdate(calendarDTO);
        } else if(taskEvent.getAction() == Actions.DELETED){
            calendarService.deleteEvents(calendarDTO);
        } else {
            log.info("Unknown Action Found on the Calendar Event: {}", taskEvent.getAction());
        }
    }


}
