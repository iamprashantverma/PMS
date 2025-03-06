package com.pms.activitytrackingservice.consumer;

import com.pms.activitytrackingservice.dto.CalendarDTO;
import com.pms.activitytrackingservice.entities.enums.Actions;
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
    public void handleCalendarEvent(CalendarDTO calendarDTO){

            log.info("Calendar event successfully Received {} ",calendarDTO);

            if (calendarDTO.getAction() == Actions.CREATED) {
                calendarService.createEvent(calendarDTO);
            } else if(calendarDTO.getAction() == Actions.UPDATED) {
                calendarService.updateEvent(calendarDTO);
            } else {
                log.info("Unknown Action Found on the Calendar Event {}",calendarDTO.getAction());
            }
    }
}
