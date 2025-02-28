package com.pms.TaskService.repository;

import com.pms.TaskService.entities.Story;
import com.pms.TaskService.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
    List<Story> findByProjectId(String projectId);

    List<Story> findByStatus(Status status);
}
