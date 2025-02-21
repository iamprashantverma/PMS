package com.pms.projectservice.repositories;

import com.pms.projectservice.entities.Project;
import com.pms.projectservice.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,String> {
    List<Project> findAllByStatus(Status status);

    List<Project> findAllByTitle(String title);
}
