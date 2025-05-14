package com.pms.TaskService.repositories;

import com.pms.TaskService.entities.Epic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpicRepository extends JpaRepository<Epic, String> {
    List<Epic> findAllByProjectId(String projectId);
}
