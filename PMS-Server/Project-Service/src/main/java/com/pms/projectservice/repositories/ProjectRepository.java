package com.pms.projectservice.repositories;

import com.pms.projectservice.entities.Project;
import com.pms.projectservice.entities.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,String> {
    List<Project> findAllByStatus(Status status);

    List<Project> findAllByTitle(String title);

    @Query("SELECT p FROM Project p WHERE (p.projectCreator = :userId OR :userId IN elements(p.memberIds)) AND p.status <> 'COMPLETED'")
    Page<Project> findByCreatorOrMemberAndStatusNotCompleted(@Param("userId") String userId, Pageable pageable);


}
