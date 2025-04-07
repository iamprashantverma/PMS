package com.pms.TaskService.repository;

import com.pms.TaskService.entities.SubTask;
import com.pms.TaskService.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, String> {
    List<SubTask> findAllByParentTask(Task task);
    @Query("SELECT s FROM SubTask s JOIN s.assignees a WHERE a = :userId")
    List<SubTask> findAllSubTasksByAssignee( String userId);
    List<SubTask> findAllByProjectId(String projectId);
}
