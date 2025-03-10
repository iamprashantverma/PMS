package com.pms.activitytrackingservice.repositories;

import com.pms.activitytrackingservice.entities.Calendar;
import com.pms.TaskService.event.enums.Priority;
import com.pms.TaskService.event.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarRepository  extends JpaRepository<Calendar,Long> {

    List<Calendar> findAllByCreateDateBetween(LocalDate startDate, LocalDate endDate);


    List<Calendar> findAllByAssigneesContaining(String userId);


    List<Calendar> findAllByPriority(Priority priority);

    Long findByEntityId(String entityId);

    List<Calendar> findAllByNewStatus(Status status);
}
