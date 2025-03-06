package com.pms.activitytrackingservice.services.impl;

import com.pms.activitytrackingservice.dto.CalendarDTO;
import com.pms.activitytrackingservice.entities.Calendar;
import com.pms.activitytrackingservice.entities.enums.Priority;
import com.pms.activitytrackingservice.entities.enums.Status;
import com.pms.activitytrackingservice.repositories.CalendarRepository;
import com.pms.activitytrackingservice.services.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final ModelMapper modelMapper;
    private final CalendarRepository calendarRepository;


    @Override
    public List<CalendarDTO> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {

        log.info("Fetching events from {} to {}", startDate, endDate);

        List<Calendar> taskEvents = calendarRepository.findAllByCreateDateBetween(startDate, endDate);
            return  taskEvents
                .stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public List<CalendarDTO> getEventsByUser(String userId) {
        log.info("Fetching events for user: {}", userId);
        List<Calendar> calendars = calendarRepository.findAllByAssigneesContaining(userId);
        return  calendars
                .stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<CalendarDTO> getEventsByStatus(Status status) {
        log.info("Fetching events with status: {}", status);
        List<Calendar> calendars = calendarRepository.findAllByStatus(status);
        return  calendars
                .stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<CalendarDTO> getEventsByPriority(Priority priority) {
        log.info("Fetching events with priority: {}", priority);
        List<Calendar> calendars = calendarRepository.findAllByPriority(priority);
        return calendars
                .stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public void createEvent(CalendarDTO calendarDTO) {
        log.info("Creating new event...");
        Calendar toBeCreated = modelMapper.map(calendarDTO,Calendar.class);
        Calendar savedCalendar = calendarRepository.save(toBeCreated);

        modelMapper.map(savedCalendar, CalendarDTO.class);
    }


    @Override
    public void updateEvent(CalendarDTO calendarDTO) {

        String entityId = calendarDTO.getEntityId();

        Long eventId = calendarRepository.findByEntityId(entityId);

        log.info("Updating event with ID: {}", eventId);

        Calendar toBeModified = modelMapper.map(calendarDTO,Calendar.class);
        if (toBeModified.getId() != null)
                toBeModified.setId(eventId);
        Calendar savedCalendar = calendarRepository.save(toBeModified);
        modelMapper.map(savedCalendar, CalendarDTO.class);

    }


    @Override
    public void deleteEvent(Long eventId) {
        log.info("Deleting event with ID: {}", eventId);
        calendarRepository.deleteById(eventId);

    }

}
