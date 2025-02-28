package com.pms.TaskService.repository;

import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findAllByProjectId(String project);

    List<Task> findAllByStatus(Status status);

    List<Task> findByAssigneesContains(String userId);

    List<Task> findByProjectId(String projectId);
}
