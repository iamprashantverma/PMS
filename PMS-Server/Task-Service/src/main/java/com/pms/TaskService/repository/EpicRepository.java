package com.pms.TaskService.repository;

import com.pms.TaskService.entities.Epic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpicRepository extends JpaRepository<Epic, String> {
}
