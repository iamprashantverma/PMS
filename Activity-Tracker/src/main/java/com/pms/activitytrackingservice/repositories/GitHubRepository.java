package com.pms.activitytrackingservice.repositories;

import com.pms.activitytrackingservice.entities.GitHub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GitHubRepository extends JpaRepository<GitHub,Long> {
    List<GitHub> findAllByRepositoryName(String repositoryName);

    List<GitHub> findAllByAuthor(String author);
}
