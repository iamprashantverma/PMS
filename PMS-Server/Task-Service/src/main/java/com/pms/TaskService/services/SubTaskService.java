package com.pms.TaskService.services;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.entities.SubTask;
import com.pms.TaskService.exceptions.ResourceNotFound;

import java.util.List;

/**
 * Service interface for handling operations related to SubTasks.
 */
public interface SubTaskService {

    /**
     * Creates a new SubTask and returns the created SubTaskDTO.
     *
     * @param subTaskDTO the SubTask data transfer object containing the details of the subtask
     * @return the created SubTaskDTO containing the details of the saved subtask
     */
    SubTaskDTO createSubTask(SubTaskDTO subTaskDTO);

    /**
     * Deletes an existing SubTask by its ID.
     *
     * @param subTaskId the ID of the subtask to be deleted
     * @return a ResponseDTO indicating the result of the operation
     */
    ResponseDTO deleteSubTask(String subTaskId);

    /**
     * Retrieves a SubTask by its ID.
     *
     * @param subTaskId the ID of the subtask to be retrieved
     * @return the SubTask entity corresponding to the provided ID
     * @throws ResourceNotFound if the subtask with the provided ID does not exist
     */
    SubTaskDTO getSubTaskById(String subTaskId);

    /**
     * Adds an existing SubTask to a specific Task by their respective IDs.
     *
     * @param taskId the ID of the task to which the subtask will be added
     * @param subTaskId the ID of the subtask to be added to the task
     * @return a ResponseDTO indicating the result of the operation
     * @throws ResourceNotFound if the task or subtask with the provided IDs does not exist
     */
    ResponseDTO addSubTaskOnTask(String taskId, String subTaskId);

    /**
     * Retrieves all SubTasks.
     *
     * @return a list of SubTaskDTO containing all the existing subtasks
     */
    List<SubTaskDTO> getAllSubTasks();

    /**
     * Retrieves all SubTasks that are associated with a specific Task.
     *
     * @param taskId the ID of the task whose subtasks need to be fetched
     * @return a list of SubTaskDTO containing all the subtasks associated with the given task ID
     */
    List<SubTaskDTO> getSubTasksByTaskId(String taskId);

    /**
     * Assigns a member to the specified task.
     *
     * @param taskId   the ID of the task to which the member should be assigned
     * @param memberId the ID of the member to be assigned to the task
     * @return the updated TaskDTO after assigning the member
     */
    SubTaskDTO assignMemberToSubTask(String taskId, String memberId);

    /**
     * Unassigns a member from the specified task.
     *
     * @param taskId   the ID of the task from which the member should be removed
     * @param memberId the ID of the member to be unassigned from the task
     * @return the updated TaskDTO after unassigning the member
     */
    SubTaskDTO unAssignedMemberFromTask(String taskId, String memberId);

}
