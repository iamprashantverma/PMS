package com.pms.activitytrackingservice.services;

import com.pms.activitytrackingservice.dto.GitHubDTO;
import java.util.List;

/**
 * Service interface for managing GitHub change events.
 */
public interface GitHubService {

    /**
     * Saves a new GitHub change event.
     *
     * @param gitHubChange The GitHub change event to save.

     */
    void saveGitHubChange(GitHubDTO gitHubChange);

    /**
     * Retrieves all GitHub change events.
     *
     * @return A list of all GitHub change events.
     */
    List<GitHubDTO> getAllGitHubChanges();


    /**
     * Retrieves GitHub change events by repository name.
     *
     * @param repositoryName The name of the repository.
     * @return A list of GitHub change events for the specified repository.
     */
    List<GitHubDTO> getChangesByRepository(String repositoryName);

    /**
     * Retrieves GitHub change events by author.
     *
     * @param author The author of the commits.
     * @return A list of GitHub change events by the specified author.
     */
    List<GitHubDTO> getChangesByAuthor(String author);

}
