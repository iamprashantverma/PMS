package com.pms.TaskService.repositories;

import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByAssigneesContains(String userId);

    List<Task> findByProjectId(String projectId);

    List<Task> findAllByStatusAndEpic_Id(Status status, String epicId);

    List<Task> findAllByEpic_Id(String epicId);

    @Query("SELECT t FROM Task t JOIN t.assignees a WHERE a = :userId")
    List<Task> findAllByAssignee(String userId);
}
