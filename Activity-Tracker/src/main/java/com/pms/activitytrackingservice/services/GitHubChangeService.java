package com.pms.activitytrackingservice.services;

import com.pms.activitytrackingservice.dto.GitHubChangeDTO;
import java.util.List;

/**
 * Service interface for managing GitHub change events.
 */
public interface GitHubChangeService {

    /**
     * Saves a new GitHub change event.
     *
     * @param gitHubChange The GitHub change event to save.
     * @return The saved GitHub change event.
     */
    GitHubChangeDTO saveGitHubChange(GitHubChangeDTO gitHubChange);

    /**
     * Retrieves all GitHub change events.
     *
     * @return A list of all GitHub change events.
     */
    List<GitHubChangeDTO> getAllGitHubChanges();

    /**
     * Retrieves GitHub change events by repository name.
     *
     * @param repositoryName The name of the repository.
     * @return A list of GitHub change events for the specified repository.
     */
//    List<GitHubChangeDTO> getChangesByRepository(String repositoryName);

    /**
     * Retrieves GitHub change events by author.
     *
     * @param author The author of the commits.
     * @return A list of GitHub change events by the specified author.
     */
//    List<GitHubChangeDTO> getChangesByAuthor(String author);

    /**
     * Retrieves a specific GitHub change event by its ID.
     *
     * @param id The ID of the GitHub change event.
     * @return The GitHub change event, if found.
     */
//    GitHubChangeDTO getGitHubChangeById(Long id);
}
