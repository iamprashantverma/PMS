package com.pms.activitytrackingservice.repositories;

import com.pms.activitytrackingservice.entities.GitHubChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GitHubChangeRepository extends JpaRepository<GitHubChange,Long> {
    List<GitHubChange> findAllByRepositoryName(String repositoryName);

    List<GitHubChange> findAllByAuthor(String author);
}
