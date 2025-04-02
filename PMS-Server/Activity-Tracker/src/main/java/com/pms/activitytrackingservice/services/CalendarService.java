package com.pms.activitytrackingservice.services;


import com.pms.activitytrackingservice.dto.CalendarDTO;
import com.pms.TaskService.event.enums.Priority;
import com.pms.TaskService.event.enums.Status;

import java.time.LocalDate;
import java.util.List;

public interface CalendarService {

    /**
     * Retrieves all task events within a specified date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A CalendarDTO containing TaskEventDTOs within the given date range.
     */
    List<CalendarDTO> getEventsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves all task events assigned to a specific user.
     *
     * @param userId The ID of the user.
     * @return A CalendarDTO containing TaskEventDTOs assigned to the given user.
     */
    List<CalendarDTO> getEventsByUser(String userId);

    /**
     * Retrieves task events based on their status (e.g., OPEN, IN_PROGRESS, DONE).
     *
     * @param status The status of the task.
     * @return A CalendarDTO containing TaskEventDTOs matching the given status.
     */
   List< CalendarDTO > getEventsByStatus(Status status);

    /**
     * Retrieves task events based on priority (e.g., HIGH, MEDIUM, LOW).
     *
     * @param priority The priority of the task.
     * @return A CalendarDTO containing TaskEventDTOs matching the given priority.
     */
    List<CalendarDTO> getEventsByPriority(Priority priority);

    /**
     * Adds a new task event to the calendar.
     *
     * @param taskEvent TaskEventDTO object to be added.
     */
    void createEvent(CalendarDTO taskEvent);

    /**
     * Updates an existing task event.
     *
     * @param taskEventDTO The updated TaskEventDTO object.
     */
    void updateEvent(CalendarDTO taskEventDTO);

    /**
     * Deletes a task event from the calendar.
     *
     * @param eventId The unique ID of the task event to be deleted.
     */
    void deleteEvent(Long eventId);

    List<CalendarDTO> findAllEventsByProjectId(String projectId);
}
