package com.pms.TaskService.services;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.exceptions.ResourceNotFound;

import java.util.List;

/**
 * Service interface for handling operations related to SubTasks.
 */
public interface SubTaskService {

    /**
     * Creates a new SubTask.
     *
     * @param subTaskDTO The DTO containing subtask details.
     * @return The created SubTaskDTO.
     */
    SubTaskDTO createSubTask(SubTaskDTO subTaskDTO);

    /**
     * Deletes a SubTask by its ID.
     *
     * @param subTaskId The ID of the subtask to delete.
     * @return A ResponseDTO indicating the result.
     */
    ResponseDTO deleteSubTask(String subTaskId);

    /**
     * Retrieves a SubTask by its ID.
     *
     * @param subTaskId The ID of the subtask to retrieve.
     * @return The SubTaskDTO of the requested subtask.
     * @throws ResourceNotFound if the subtask is not found.
     */
    SubTaskDTO getSubTaskById(String subTaskId);

    /**
     * Associates an existing SubTask with a specific Task.
     *
     * @param taskId     The ID of the task.
     * @param subTaskId  The ID of the subtask to associate.
     * @return A ResponseDTO indicating the result.
     * @throws ResourceNotFound if the task or subtask is not found.
     */
    ResponseDTO addSubTaskOnTask(String taskId, String subTaskId);

    /**
     * Retrieves all SubTasks.
     *
     * @return A list of all SubTaskDTOs.
     */
    List<SubTaskDTO> getAllSubTasks();

    /**
     * Retrieves all SubTasks associated with a specific Task.
     *
     * @param taskId The ID of the task.
     * @return A list of SubTaskDTOs linked to the task.
     */
    List<SubTaskDTO> getSubTasksByTaskId(String taskId);

    /**
     * Assigns a member to the specified SubTask.
     *
     * @param subTaskId The ID of the subtask.
     * @param memberId  The ID of the member to assign.
     * @return The updated SubTaskDTO.
     */
    SubTaskDTO assignMemberToSubTask(String subTaskId, String memberId);

    /**
     * Unassigns a member from the specified SubTask.
     *
     * @param subTaskId The ID of the subtask.
     * @param memberId  The ID of the member to unassign.
     * @return The updated SubTaskDTO.
     */
    SubTaskDTO unAssignedMemberFromTask(String subTaskId, String memberId);

    /**
     * Changes the status of a SubTask.
     *
     * @param subTaskId The ID of the subtask.
     * @param status    The new status to apply.
     * @return The updated SubTaskDTO.
     */
    SubTaskDTO changeSubTaskStatus(String subTaskId, Status status);

    /**
     * Retrieves all SubTasks associated with a specific Project.
     *
     * @param projectId The ID of the project.
     * @return A list of SubTaskDTOs linked to the project.
     */
    List<SubTaskDTO> getSubTasksByProjectId(String projectId);
}
