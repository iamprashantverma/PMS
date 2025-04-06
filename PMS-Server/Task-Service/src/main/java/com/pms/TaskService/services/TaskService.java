package com.pms.TaskService.services;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.exceptions.ResourceNotFound;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing tasks within the Task Management System.
 * Defines operations to create, update, delete, retrieve, and assign tasks.
 */
public interface TaskService {

    /**
     * Creates a new task with an optional file attachment.
     *
     * @param taskDTO The DTO containing task details.
     * @param file    The file to attach with the task.
     * @return The created task as a TaskDTO.
     */
    TaskDTO createTask(TaskDTO taskDTO, MultipartFile file);

    /**
     * Retrieves a task by its unique ID.
     *
     * @param taskId The ID of the task.
     * @return The retrieved TaskDTO.
     * @throws ResourceNotFound if the task is not found.
     */
    TaskDTO getTaskById(String taskId);

    /**
     * Deletes a task by its ID.
     *
     * @param taskId The ID of the task.
     * @return A ResponseDTO indicating the result.
     * @throws ResourceNotFound if the task is not found.
     */
    ResponseDTO deleteTask(String taskId);

    /**
     * Updates an existing task.
     *
     * @param taskDTO The updated task data.
     * @return The updated TaskDTO.
     * @throws ResourceNotFound if the task is not found.
     */
    TaskDTO updateTask(TaskDTO taskDTO);

    /**
     * Retrieves all tasks under a specific project.
     *
     * @param projectId The ID of the project.
     * @return A list of TaskDTOs.
     */
    List<TaskDTO> getAllTaskByProjectId(String projectId);

    /**
     * Adds a task to a specific epic.
     *
     * @param epicId The ID of the epic.
     * @param task   The task to be added.
     * @throws ResourceNotFound if the epic or task is not found.
     */
    void addTaskOnEpic(String epicId, Task task);

    /**
     * Retrieves all tasks in the system.
     *
     * @return A list of all TaskDTOs.
     */
    List<TaskDTO> getAllTasks();

    /**
     * Retrieves tasks assigned to a specific user.
     *
     * @param userId The user's ID.
     * @return A list of TaskDTOs assigned to the user.
     */
    List<TaskDTO> getTasksByUserId(String userId);

    /**
     * Retrieves tasks by status within a specific epic.
     *
     * @param status  The status filter.
     * @param epicId  The epic ID.
     * @return A list of matching TaskDTOs.
     */
    List<TaskDTO> getTasksByStatus(Status status, String epicId);

    /**
     * Assigns a member to a task.
     *
     * @param taskId   The ID of the task.
     * @param memberId The ID of the member.
     * @return The updated TaskDTO.
     */
    TaskDTO assignMemberToTask(String taskId, String memberId);

    /**
     * Unassigns a member from a task.
     *
     * @param taskId   The ID of the task.
     * @param memberId The ID of the member.
     * @return The updated TaskDTO.
     */
    TaskDTO unAssignedMemberFromTask(String taskId, String memberId);

    /**
     * Changes the status of a task.
     *
     * @param taskId The ID of the task.
     * @param status The new status.
     * @return The updated TaskDTO.
     */
    TaskDTO changeTaskStatus(String taskId, Status status);

    /**
     * Assigns a new parent to the task (e.g., epic, story, etc.).
     *
     * @param parentId The ID of the new parent entity.
     * @param taskId   The ID of the task.
     * @param parent   The type of parent (e.g., "epic", "story").
     * @return The updated TaskDTO.
     */
    TaskDTO assignNewParent(String parentId, String taskId, String parent);

    /**
     * Retrieves tasks under a specific epic.
     *
     * @param epicId The ID of the epic.
     * @return A list of TaskDTOs.
     */
    List<TaskDTO> getTaskByEpicId(String epicId);
}
