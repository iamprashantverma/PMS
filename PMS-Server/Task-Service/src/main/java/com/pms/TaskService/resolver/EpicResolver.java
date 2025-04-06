package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.UserDTO;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.EpicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * GraphQL Resolver for managing Epic-related operations.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class EpicResolver {

    private final EpicService epicService;

    /**
     * Creates a new Epic.
     *
     * @param epicDTO the epic details
     * @param file optional image file
     * @return the created EpicDTO
     */
    @MutationMapping
    public EpicDTO createEpic(@Argument("epic") EpicDTO epicDTO, @Argument("image") MultipartFile file) {
        log.info("Creating Epic: {}, image attached: {}", epicDTO, file != null);
        return epicService.createEpic(epicDTO); // You may want to handle file here too
    }

    /**
     * Deletes an Epic by ID.
     *
     * @param epicId the ID of the Epic to delete
     * @return the deleted EpicDTO
     */
    @MutationMapping
    public EpicDTO deleteEpic(@Argument String epicId) {
        log.info("Deleting Epic with ID: {}", epicId);
        return epicService.deleteEpic(epicId);
    }

    /**
     * Retrieves an Epic by ID.
     *
     * @param epicId the Epic ID
     * @return the EpicDTO
     */
    @QueryMapping
    public EpicDTO getEpicById(@Argument String epicId) {
        log.info("Fetching Epic by ID: {}", epicId);
        return epicService.getEpicById(epicId);
    }

    /**
     * Retrieves all Epics associated with a project.
     *
     * @param projectId the project ID
     * @return list of EpicDTOs
     */
    @QueryMapping
    public List<EpicDTO> getAllEpicsByProjectId(@Argument String projectId) {
        log.info("Fetching all Epics for Project ID: {}", projectId);
        return epicService.getAllEpicsByProjectId(projectId);
    }

    /**
     * Updates the status of an Epic.
     *
     * @param epicId the Epic ID
     * @param status the new status
     * @return updated EpicDTO
     */
    @MutationMapping
    public EpicDTO updateEpicStatus(@Argument String epicId, @Argument Status status) {
        log.info("Updating Epic status - ID: {}, Status: {}", epicId, status);
        return epicService.updateEpicStatus(epicId, status);
    }

    /**
     * Assigns a member to an Epic.
     *
     * @param epicId the Epic ID
     * @param memberId the Member ID
     * @return updated EpicDTO
     */
    @MutationMapping
    public EpicDTO assignMemberToEpic(@Argument String epicId, @Argument String memberId) {
        log.info("Assigning member {} to Epic {}", memberId, epicId);
        return epicService.assignMemberToEpic(epicId, memberId);
    }

    /**
     * Removes a member from an Epic.
     *
     * @param epicId the Epic ID
     * @param memberId the Member ID
     * @return updated EpicDTO
     */
    @MutationMapping
    public EpicDTO removeMemberFromEpic(@Argument String epicId, @Argument String memberId) {
        log.info("Removing member {} from Epic {}", memberId, epicId);
        return epicService.removeMemberFromEpic(epicId, memberId);
    }

    /**
     * Retrieves all members assigned to an Epic.
     *
     * @param epicId the Epic ID
     * @return list of assigned UserDTOs
     */
    @QueryMapping
    public List<UserDTO> getAssignedMembers(@Argument String epicId) {
        log.info("Fetching assigned members for Epic ID: {}", epicId);
        return epicService.getAssignedMembers(epicId);
    }
}
