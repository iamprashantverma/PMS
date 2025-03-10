package com.pms.activitytrackingservice.controller;

import com.pms.activitytrackingservice.dto.CalendarDTO;
import com.pms.TaskService.event.enums.Priority;
import com.pms.TaskService.event.enums.Status;
import com.pms.activitytrackingservice.services.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/date-range")
    public ResponseEntity<List<CalendarDTO>> getEventsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(calendarService.getEventsByDateRange(startDate, endDate));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CalendarDTO>> getEventsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(calendarService.getEventsByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CalendarDTO>> getEventsByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(calendarService.getEventsByStatus(status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<CalendarDTO>> getEventsByPriority(@PathVariable Priority  priority) {
        return ResponseEntity.ok(calendarService.getEventsByPriority(priority));
    }



    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        calendarService.deleteEvent(eventId);
        return ResponseEntity.ok().build();
    }
}
