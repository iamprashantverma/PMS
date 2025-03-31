package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.BugDTO;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.BugService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Resolver class for handling GraphQL operations related to bugs.
 * Provides mutation and query mappings to create, update, delete, assign, change status,
 * and retrieve bugs.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class BugResolver {

    private final BugService bugService;

    /**
     * Creates a new bug.
     *
     * @param bugInput the input data for the new bug
     * @return the created BugDTO
     */
    @MutationMapping
    public BugDTO createBug(@Argument("bugInput") BugDTO bugInput, @Argument("image")MultipartFile file) {
        log.info("Creating bug with input: {} and file{}", bugInput,file);
        return bugService.createBug(bugInput);
    }

    /**
     * Updates an existing bug.
     *
     * @param bugDTO the bug input data to update
     * @return the updated BugDTO
     */
    @MutationMapping
    public BugDTO updateBug(@Argument("bugDTO") BugDTO bugDTO) {
        log.info("Updating bug with input: {}", bugDTO);
        return bugService.updateBug(bugDTO);
    }

    /**
     * Deletes a bug by its ID.
     *
     * @param bugId the ID of the bug to delete
     * @return the deleted BugDTO
     */
    @MutationMapping
    public ResponseDTO deleteBug(@Argument("bugId") String bugId) {
        log.info("Deleting bug with id: {}", bugId);
        return bugService.deleteBug(bugId);
    }

    /**
     * Assigns a bug to a user.
     *
     * @param bugId  the ID of the bug to assign
     * @param userId the ID of the user to whom the bug will be assigned
     * @return the updated BugDTO after assignment
     */
    @MutationMapping
    public ResponseDTO assignBugToUser(@Argument String bugId, @Argument String userId) {
        log.info("Assigning bug with id {} to user with id {}", bugId, userId);
        return bugService.assignBugToUser(bugId, userId);
    }

    /**
     * Changes the status of a bug.
     *
     * @param bugId  the ID of the bug whose status is to be changed
     * @param status the new status to set
     * @return the updated BugDTO after status change
     */
    @MutationMapping
    public ResponseDTO changeBugStatus(@Argument("bugId") String bugId, @Argument("status") Status status) {
        log.info("Changing status for bug with id {} to {}", bugId, status);
        return bugService.changeBugStatus(bugId, status);
    }

    // Query mappings

    /**
     * Retrieves a bug by its ID.
     *
     * @param bugId the ID of the bug to retrieve (mapped from the argument "budId")
     * @return the BugDTO corresponding to the provided ID
     */
    @QueryMapping
    public BugDTO getBugById(@Argument("bugId") String bugId) {
        log.info("Fetching bug with id: {}", bugId);
        return bugService.getBugById(bugId);
    }

    /**
     * Retrieves a list of bugs associated with a specific project.
     *
     * @param projectId the ID of the project for which bugs are to be fetched
     * @return a list of BugDTOs associated with the specified project
     */
    @QueryMapping
    public List<BugDTO> getBugsByProjectId(@Argument("projectId") String projectId) {
        log.info("Fetching bugs for project id: {}", projectId);
        return bugService.getBugsByProjectId(projectId);
    }

    /**
     * Retrieves a list of bugs assigned to a specific user.
     *
     * @param userId the ID of the user for which bugs are to be fetched
     * @return a list of BugDTOs associated with the specified user
     */
    @QueryMapping
    public List<BugDTO> getBugsByUserId(@Argument("userId") String userId) {
        log.info("Fetching bugs for user id: {}", userId);
        return bugService.getBugsByUserId(userId);
    }


}
