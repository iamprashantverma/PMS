package com.pms.activitytrackingservice.repositories;

import com.pms.activitytrackingservice.entities.Calendar;
import com.pms.activitytrackingservice.entities.enums.Priority;
import com.pms.activitytrackingservice.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository  extends JpaRepository<Calendar,Long> {

    List<Calendar> findAllByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);


    List<Calendar> findAllByAssigneesContaining(String userId);

    List<Calendar> findAllByStatus(Status status);

    List<Calendar> findAllByPriority(Priority priority);

    Long findByEntityId(String entityId);

}
