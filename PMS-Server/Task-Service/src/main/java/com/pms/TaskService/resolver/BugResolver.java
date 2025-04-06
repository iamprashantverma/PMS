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
 * GraphQL resolver for managing bug-related operations.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class BugResolver {

    private final BugService bugService;

    /**
     * Creates a new bug with optional image upload.
     *
     * @param bugInput the bug details
     * @param file     optional image file
     * @return the created bug
     */
    @MutationMapping
    public BugDTO createBug(@Argument("bugInput") BugDTO bugInput,
                            @Argument("image") MultipartFile file) {
        log.info("Creating bug: {}, with file: {}", bugInput, file.getOriginalFilename());
        return bugService.createBug(bugInput, file);
    }

    /**
     * Updates an existing bug.
     *
     * @param bugDTO updated bug data
     * @return the updated bug
     */
    @MutationMapping
    public BugDTO updateBug(@Argument("bugDTO") BugDTO bugDTO) {
        log.info("Updating bug: {}", bugDTO);
        return bugService.updateBug(bugDTO);
    }

    /**
     * Deletes a bug by ID.
     *
     * @param bugId the bug ID
     * @return result message
     */
    @MutationMapping
    public ResponseDTO deleteBug(@Argument String bugId) {
        log.info("Deleting bug with ID: {}", bugId);
        return bugService.deleteBug(bugId);
    }

    /**
     * Assigns a bug to a user.
     *
     * @param bugId  the bug ID
     * @param userId the user ID
     * @return result message
     */
    @MutationMapping
    public ResponseDTO assignBugToUser(@Argument String bugId,
                                       @Argument String userId) {
        log.info("Assigning bug {} to user {}", bugId, userId);
        return bugService.assignBugToUser(bugId, userId);
    }

    /**
     * Changes the status of a bug.
     *
     * @param bugId  the bug ID
     * @param status new status
     * @return result message
     */
    @MutationMapping
    public ResponseDTO changeBugStatus(@Argument String bugId,
                                       @Argument Status status) {
        log.info("Changing status of bug {} to {}", bugId, status);
        return bugService.changeBugStatus(bugId, status);
    }

    /**
     * Retrieves a bug by ID.
     *
     * @param bugId the bug ID
     * @return the bug
     */
    @QueryMapping
    public BugDTO getBugById(@Argument String bugId) {
        log.info("Fetching bug with ID: {}", bugId);
        return bugService.getBugById(bugId);
    }

    /**
     * Retrieves all bugs associated with a project.
     *
     * @param projectId the project ID
     * @return list of bugs
     */
    @QueryMapping
    public List<BugDTO> getBugsByProjectId(@Argument String projectId) {
        log.info("Fetching bugs for project ID: {}", projectId);
        return bugService.getBugsByProjectId(projectId);
    }

    /**
     * Retrieves all bugs assigned to a specific user.
     *
     * @param userId the user ID
     * @return list of bugs
     */
    @QueryMapping
    public List<BugDTO> getBugsByUserId(@Argument String userId) {
        log.info("Fetching bugs for user ID: {}", userId);
        return bugService.getBugsByUserId(userId);
    }
}
