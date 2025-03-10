package com.pms.TaskService.services;

import com.pms.TaskService.dto.*;
import com.pms.TaskService.entities.Story;
import com.pms.TaskService.entities.enums.Status;

import java.util.List;

/**
 * Service interface for managing Stories in the Task Service.
 */
public interface StoryService {

    /**
     * Creates a new Story.
     *
     * @param storyDTO The Story data transfer object containing story details.
     * @return The created Story as a StoryDTO.
     */
    StoryDTO createStory(StoryDTO storyDTO);

    /**
     * Updates an existing Story.
     *
     * @param storyDTO The updated Story data transfer object.
     * @return The updated Story as a StoryDTO.
     */
    StoryDTO updateStory(StoryDTO storyDTO);

    /**
     * Deletes a Story by its ID.
     *
     * @param storyId The unique identifier of the Story to be deleted.
     * @return The deleted Story as a StoryDTO.
     */
    StoryDTO deleteStory(String storyId);

    /**
     * Retrieves a Story by its ID.
     *
     * @param storyId The unique identifier of the Story.
     * @return The requested Story as a StoryDTO.
     */
    StoryDTO getStoryById(String storyId);

    /**
     * Retrieves all Stories associated with a given project or task.
     *
     * @param projectId The unique identifier of the project.
     * @return A list of StoryDTOs associated with the project.
     */
    List<StoryDTO> getAllStoriesByProjectId(String projectId);

    /**
     * Retrieves all Stories assigned to a specific user.
     *
     * @param userId The unique identifier of the user.
     * @return A list of StoryDTOs assigned to the user.
     */
    List<StoryDTO> getAllStoriesByUserId(String userId);

    /**
     * Assigns a user to a specific story.
     *
     * @param storyId The unique identifier of the story.
     * @param userId  The unique identifier of the user to be assigned to the story.
     * @return The updated StoryDTO with the assigned user.
     */
    StoryDTO assignUserToStory(String storyId, String userId);

    /**
     * Unassigns a user from a specific story.
     *
     * @param storyId The unique identifier of the story.
     * @param userId  The unique identifier of the user to be unassigned from the story.
     * @return The updated StoryDTO with the user unassigned.
     */
    StoryDTO unassignUserFromStory(String storyId, String userId);

    /**
     * Changes the status of a Story.
     *
     * @param storyId The unique identifier of the story.
     * @param status  The new status to set for the story (e.g., "In Progress", "Completed").
     * @return The updated StoryDTO with the new status.
     */
    StoryDTO changeStoryStatus(String storyId, Status status);
    /**
     * Retrieves all Stories with a specific status.
     *
     * @param status The status to filter the stories by.
     * @return A list of StoryDTOs with the specified status.
     */
    List<StoryDTO> getStoriesByStatus(Status status);
    /**
     * @param epicId to fetch Epic
     * @param story  entity
     */
    void addStoryOnEpic(String epicId, Story story);
}
