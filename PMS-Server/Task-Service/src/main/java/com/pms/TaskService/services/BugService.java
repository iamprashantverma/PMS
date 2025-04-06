package com.pms.TaskService.services;

import com.pms.TaskService.dto.BugDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.enums.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing bugs within the system.
 * Provides methods to create, update, delete, assign, comment, and manage bugs.
 */
public interface BugService {

    /**
     * Creates a new bug.
     *
     * @param bugDTO The data transfer object containing the bug details.
     * @return The created bug as a BugDTO.
     */
    BugDTO createBug(BugDTO bugDTO, MultipartFile file);

    /**
     * Retrieves a bug by its unique identifier.
     *
     * @param bugId The unique identifier of the bug to retrieve.
     * @return The bug as a BugDTO.
     */
    BugDTO getBugById(String bugId);

    /**
     * Updates an existing bug with new information.
     *
     * @param bugDTO The data transfer object containing the updated bug details.

     * @return The updated bug as a BugDTO.
     */
    BugDTO updateBug(BugDTO bugDTO);

    /**
     * Deletes a bug by its unique identifier.
     *
     * @param bugId The unique identifier of the bug to delete.
     * @return A response indicating success or failure.
     */
    ResponseDTO deleteBug(String bugId);

    /**
     * Retrieves all bugs associated with a specific project.
     *
     * @param projectId The unique identifier of the project to fetch bugs for.
     * @return A list of BugDTOs associated with the project.
     */
    List<BugDTO> getBugsByProjectId(String projectId);

    /**
     * Assigns a bug to a user.
     *
     * @param bugId  The unique identifier of the bug to assign.
     * @param userId The unique identifier of the user to assign the bug to.
     * @return A response indicating success or failure.
     */
    ResponseDTO assignBugToUser(String bugId, String userId);

    /**
     * Changes the status of the bug (e.g., from "open" to "in progress", etc.).
     *
     * @param bugId The unique identifier of the bug.
     * @param status The new status of the bug.
     * @return A response indicating success or failure.
     */
    ResponseDTO changeBugStatus(String bugId, Status status);

    /**
     * Retrieves all bugs assigned to a specific user.
     *
     * @param userId The unique identifier of the user.
     * @return A list of BugDTOs assigned to the user.
     */
    List<BugDTO> getBugsByUserId(String userId);
}
