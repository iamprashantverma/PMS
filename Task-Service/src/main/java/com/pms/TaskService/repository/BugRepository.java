package com.pms.TaskService.repository;

import com.pms.TaskService.entities.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository  extends JpaRepository<Bug,String> {
    List<Bug> findByProjectId(String projectId);

    List<Bug> findByAssignees(String userId);
}
