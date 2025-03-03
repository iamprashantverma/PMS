package com.pms.activitytrackingservice.repositories;

import com.pms.activitytrackingservice.entities.GitHubChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitHubChangeRepository extends JpaRepository<GitHubChange,Long> {
}
