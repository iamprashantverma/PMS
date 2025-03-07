package com.pms.TaskService.services;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.exceptions.ResourceNotFound;

import java.util.List;

/**
 * Service interface for managing tasks within the Task Management System.
 * Provides methods to create, update, delete, retrieve, and manage tasks.
 */
public interface TaskService {

    /**
     * Creates a new task.
     *
     * @param taskDTO The data transfer object containing the task details.
     * @return The created task as a TaskDTO.
     */
    public TaskDTO createTask(TaskDTO taskDTO);

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param taskId The unique identifier of the task to retrieve.
     * @return The task as a TaskDTO.
     * @throws ResourceNotFound if the task with the given ID does not exist.
     */
    TaskDTO getTaskById(String taskId);

    /**
     * Deletes a task by its unique identifier.
     *
     * @param taskId The unique identifier of the task to delete.
     * @return A boolean indicating whether the task was successfully deleted.
     * @throws ResourceNotFound if the task with the given ID does not exist.
     */
    ResponseDTO deleteTask(String taskId);

    /**
     * Updates an existing task with new information.
     *
     * @param taskDTO The data transfer object containing the updated task details.
     * @return The updated task as a TaskDTO.
     * @throws ResourceNotFound if the task with the given ID does not exist.
     */
    TaskDTO updateTask(TaskDTO taskDTO);

    /**
     * Retrieves all tasks associated with a specific project.
     *
     * @param projectId The unique identifier of the project to fetch tasks for.
     * @return A list of TaskDTOs associated with the project.
     */
    List<TaskDTO> getAllTaskByProjectId(String projectId);

    /**
     * Adds a task to a specific epic.
     *
     * @param epicId The unique identifier of the epic.
     * @param task   The unique identifier of the task to add to the epic.
     * @throws ResourceNotFound if the epic or task with the given ID does not exist.
     */
    void addTaskOnEpic(String epicId, Task task);

    /**
     * Retrieves a list of all tasks.
     *
     * @return A list of all TaskDTOs in the system.
     */
    List<TaskDTO> getAllTasks();

    /**
     * Retrieves all tasks that are assigned to a specific user.
     *
     * @param userId The unique identifier of the user.
     * @return A list of TaskDTOs assigned to the user.
     */
    List<TaskDTO> getTasksByUserId(String userId);


    /**
     * Retrieves all tasks within a specific status (e.g., "In Progress", "Completed").
     *
     * @param status The status of the tasks to retrieve.
     * @return A list of TaskDTOs filtered by the given status.
     */
    List<TaskDTO> getTasksByStatus(Status status);

    /**
     * Assigns a member to the specified task.
     *
     * @param taskId   the ID of the task to which the member should be assigned
     * @param memberId the ID of the member to be assigned to the task
     * @return the updated TaskDTO after assigning the member
     */
    TaskDTO assignMemberToTask(String taskId, String memberId);

    /**
     * Unassigns a member from the specified task.
     *
     * @param taskId   the ID of the task from which the member should be removed
     * @param memberId the ID of the member to be unassigned from the task
     * @return the updated TaskDTO after unassigning the member
     */
    TaskDTO unAssignedMemberFromTask(String taskId, String memberId);

}
