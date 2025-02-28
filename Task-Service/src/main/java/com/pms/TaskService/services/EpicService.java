package com.pms.TaskService.services;

import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.exceptions.ResourceNotFound;

import java.util.List;

/**
 * Service interface for managing Epics.
 */
public interface EpicService {

    /**
     * Creates a new Epic.
     *
     * @param epicDTO The DTO containing epic details.
     * @return The created EpicDTO.
     */
    EpicDTO createEpic(EpicDTO epicDTO);

    /**
     * Updates an existing Epic and publishes an event if status changes.
     * @param epicId The updated Epic data.
     * @return The updated EpicDTO.
     * @throws ResourceNotFound if the Epic does not exist.
     */
    EpicDTO updateEpicStatus(String epicId, Status newStatus);

    /**
     * Deletes an Epic by its ID.
     *
     * @param epicId The ID of the Epic to delete.
     * @return The deleted EpicDTO.
     */
    EpicDTO deleteEpic(String epicId);

    /**
     * Retrieves an Epic by its ID.
     *
     * @param epicId The ID of the Epic.
     * @return The EpicDTO if found, otherwise null.
     */
    EpicDTO getEpicById(String epicId);
    /**
     * get list of all epicsDTO
     * @return get all epicsDTO
     */
    List<EpicDTO> getAllActiveEpics();

}
