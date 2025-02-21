package com.pms.TaskService.repository;

import com.pms.TaskService.entities.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, String> {
}
