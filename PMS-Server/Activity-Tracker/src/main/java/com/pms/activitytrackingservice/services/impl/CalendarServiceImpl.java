package com.pms.activitytrackingservice.services.impl;

import com.pms.activitytrackingservice.dto.CalendarDTO;
import com.pms.activitytrackingservice.entities.Calendar;
import com.pms.TaskService.event.enums.Priority;
import com.pms.TaskService.event.enums.Status;
import com.pms.activitytrackingservice.repositories.CalendarRepository;
import com.pms.activitytrackingservice.services.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final ModelMapper modelMapper;
    private final CalendarRepository calendarRepository;


    @Override
    public List<CalendarDTO> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {

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
        List<Calendar> calendars = calendarRepository.findAllByNewStatus(status);
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
        Calendar calendar = modelMapper.map(calendarDTO,Calendar.class);
        log.info("{}",calendarDTO.getEvent());
        log.info("{}",calendar);
        calendar.setEvent(calendarDTO.getEvent());
        Calendar savedCalendar = calendarRepository.save(calendar);
        modelMapper.map(savedCalendar, CalendarDTO.class);
    }

    @Override
    public void updateEvent(CalendarDTO taskEvent) {

        String entityId = taskEvent.getEntityId();

        Long eventId = calendarRepository.findByEntityId(entityId);

        log.info("Updating event with ID: {}", eventId);

        Calendar toBeModified = modelMapper.map(taskEvent,Calendar.class);

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

    @Override
    public List<CalendarDTO> findAllEventsByProjectId(String projectId) {
        List<Calendar> cds = calendarRepository.findAllByProjectId(projectId);
        return cds.stream()
                .map(calendar -> modelMapper.map(calendar,CalendarDTO.class))
                .toList();
    }

}
