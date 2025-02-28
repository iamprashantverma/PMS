package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.StoryDTO;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StoryResolver {

    private final StoryService storyService;

    /**
     * Creates a new story.
     *
     * @param story the story input data
     * @return the created StoryDTO
     */
    @MutationMapping
    public StoryDTO createStory(@Argument("story") StoryDTO story) {
        log.info("Creating story: {}", story);
        return storyService.createStory(story);
    }

    /**
     * Deletes a story by its ID.
     *
     * @param storyId the ID of the story to delete
     * @return the deleted StoryDTO
     */
    @MutationMapping
    public StoryDTO deleteStory(@Argument("storyId") String storyId) {
        log.info("Deleting story with id: {}", storyId);
        return storyService.deleteStory(storyId);
    }

    /**
     * Updates a story.
     *
     * @param storyDTO the updated story data
     * @return the updated StoryDTO
     */
    @MutationMapping
    public StoryDTO updateStory(@Argument("storyDTO") StoryDTO storyDTO) {
        log.info("Updating story: {}", storyDTO);
        return storyService.updateStory(storyDTO);
    }

    /**
     * Assigns a user to a story.
     *
     * @param storyId the ID of the story
     * @param userId the ID of the user to assign
     * @return the updated StoryDTO
     */
    @MutationMapping
    public StoryDTO assignUserToStory(@Argument("storyId") String storyId, @Argument("userId") String userId) {
        log.info("Assigning user {} to story {}", userId, storyId);
        return storyService.assignUserToStory(storyId, userId);
    }

    /**
     * Unassigns a user from a story.
     *
     * @param storyId the ID of the story
     * @param userId the ID of the user to unassign
     * @return the updated StoryDTO
     */
    @MutationMapping
    public StoryDTO unassignUserFromStory(@Argument("storyId") String storyId, @Argument("userId") String userId) {
        log.info("Unassigning user {} from story {}", userId, storyId);
        return storyService.unassignUserFromStory(storyId, userId);
    }

    /**
     * Changes the status of a story.
     * In this example, an extra argument "name" is added.
     *
     * @param storyId the ID of the story
     * @return the updated StoryDTO
     */
    @MutationMapping
    public StoryDTO changeStoryStatus(@Argument("storyId") String storyId, @Argument("status") Status status) {
        log.info("Changing status for story {} ", storyId);
        return storyService.changeStoryStatus( storyId,status);
    }

    /**
     * Retrieves a story by its ID.
     *
     * @param storyId the ID of the story
     * @return the StoryDTO
     */
    @QueryMapping
    public StoryDTO getStoryById(@Argument("storyId") String storyId) {
        log.info("Fetching story by id: {}", storyId);
        return storyService.getStoryById(storyId);
    }

    /**
     * Retrieves all stories associated with a specific project.
     *
     * @param projectId the project ID
     * @return a list of StoryDTOs
     */
    @QueryMapping
    public List<StoryDTO> getAllStoriesByProjectId(@Argument("projectId") String projectId) {
        log.info("Fetching all stories for project: {}", projectId);
        return storyService.getAllStoriesByProjectId(projectId);
    }

    /**
     * Retrieves all stories assigned to a specific user.
     *
     * @param userId the user ID
     * @return a list of StoryDTOs
     */
    @QueryMapping
    public List<StoryDTO> getAllStoriesByUserId(@Argument("userId") String userId) {
        log.info("Fetching all stories for user: {}", userId);
        return storyService.getAllStoriesByUserId(userId);
    }

    /**
     * Retrieves stories filtered by status.
     *
     * @param status the issue status to filter by
     * @return a list of StoryDTOs
     */
    @QueryMapping
    public List<StoryDTO> getStoriesByStatus(@Argument("status") Status status) {
        log.info("Fetching stories by status: {}", status);
        return storyService.getStoriesByStatus(status);
    }

}
