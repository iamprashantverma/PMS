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
        return taskEvents.stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CalendarDTO> getEventsByUser(String userId) {
        log.info("Fetching events for user: {}", userId);
        List<Calendar> calendars = calendarRepository.findAllByAssigneesContaining(userId);
        return calendars.stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CalendarDTO> getEventsByStatus(Status status) {
        log.info("Fetching events with status: {}", status);
        List<Calendar> calendars = calendarRepository.findAllByNewStatus(status);
        return calendars.stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CalendarDTO> getEventsByPriority(Priority priority) {
        log.info("Fetching events with priority: {}", priority);
        List<Calendar> calendars = calendarRepository.findAllByPriority(priority);
        return calendars.stream()
                .map(event -> modelMapper.map(event, CalendarDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void createEvent(CalendarDTO calendarDTO) {
        log.info("Creating new event...");
        Calendar calendar = modelMapper.map(calendarDTO, Calendar.class);
        calendar.setEventType(calendarDTO.getEventType());
        calendarRepository.save(calendar);
    }

    @Override
    public void updateEvent(CalendarDTO taskEvent) {
        String entityId = taskEvent.getEntityId();
        Calendar calendar = calendarRepository.findByEntityId(entityId);
        Long eventId = calendar.getId();

        log.info("Updating event with ID: {}", eventId);

        Calendar toBeModified = modelMapper.map(taskEvent, Calendar.class);

        // Ensure we're updating the existing record
        if (toBeModified.getId() != null) {
            toBeModified.setId(eventId);
        }

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
        List<Calendar> calendars = calendarRepository.findAllByProjectId(projectId);
        return calendars.stream()
                .map(calendar -> modelMapper.map(calendar, CalendarDTO.class))
                .toList();
    }

    @Override
    public void statusUpdate(CalendarDTO calendarDTO) {
        String entityId = calendarDTO.getEntityId();
        Calendar calendar = calendarRepository.findByEntityId(entityId);

        // Only status is updated here
        calendar.setCompletionPercent(calendarDTO.getCompletionPercent());
        calendar.setNewStatus(calendarDTO.getNewStatus());
        calendarRepository.save(calendar);
    }

    @Override
    public void deleteEvents(CalendarDTO calendarDTO) {
        String entityId = calendarDTO.getEntityId();
        Calendar calendar = calendarRepository.findByEntityId(entityId);

        // Instead of hard delete, mark as completed
        calendar.setNewStatus(Status.COMPLETED);
        calendar.setCompletionPercent(calendarDTO.getCompletionPercent());

        calendarRepository.save(calendar);
    }
}
