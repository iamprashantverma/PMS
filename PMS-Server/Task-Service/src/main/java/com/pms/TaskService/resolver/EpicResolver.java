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
     * @param epicDTO The DTO containing epic details.
     * @return The created EpicDTO.
     */
    @MutationMapping
    public EpicDTO createEpic(@Argument("epic") EpicDTO epicDTO) {
        log.info("Creating Epic: {}", epicDTO);
        return epicService.createEpic(epicDTO);
    }

    /**
     * Deletes an Epic by its ID.
     *
     * @param epicId The ID of the Epic to delete.
     * @return The deleted EpicDTO.
     */
    @MutationMapping
    public EpicDTO deleteEpic(@Argument("epicId") String epicId) {
        return epicService.deleteEpic(epicId);
    }

    /**
     * Retrieves an Epic by its ID.
     *
     * @param epicId The ID of the Epic.
     * @return The EpicDTO if found.
     */
    @QueryMapping
    public EpicDTO getEpicById(@Argument("epicId") String epicId) {
        return epicService.getEpicById(epicId);
    }

    /**
     * Retrieves all active Epics.
     *
     * @return List of all active EpicDTOs.
     */
    @QueryMapping
    public List<EpicDTO> getAllEpics() {
        return epicService.getAllActiveEpics();
    }

    /**
     * Updates the status of an Epic.
     *
     * @param epicId The ID of the Epic to update.
     * @param status The new status to set.
     * @return The updated EpicDTO.
     */
    @MutationMapping
    public EpicDTO updateEpicStatus(@Argument("epicId") String epicId, @Argument("status") Status status) {
        return epicService.updateEpicStatus(epicId, status);
    }

    /**
     * Assigns a member to an Epic.
     *
     * @param epicId The ID of the Epic.
     * @param memberId The ID of the member to assign.
     * @return The updated EpicDTO.
     */
    @MutationMapping
    public EpicDTO assignMemberToEpic(@Argument("epicId") String epicId, @Argument("memberId") String memberId) {
        return epicService.assignMemberToEpic(epicId, memberId);
    }

    /**
     * Removes a member from an Epic.
     *
     * @param epicId The ID of the Epic.
     * @param memberId The ID of the member to remove.
     * @return The updated EpicDTO.
     */
    @MutationMapping
    public EpicDTO removeMemberFromEpic(@Argument("epicId") String epicId, @Argument("memberId") String memberId) {
        return epicService.removeMemberFromEpic(epicId, memberId);
    }

    /**
     * Retrieves all members assigned to a given Epic.
     *
     * @param epicId The ID of the Epic.
     * @return List of assigned UserDTOs.
     */
    @QueryMapping
    public List<UserDTO> getAssignedMembers(@Argument("epicId") String epicId) {
        return epicService.getAssignedMembers(epicId);
    }
}
