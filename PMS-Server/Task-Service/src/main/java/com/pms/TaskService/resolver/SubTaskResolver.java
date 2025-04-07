package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.SubTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Resolver for managing SubTask-related operations.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class SubTaskResolver {

    private final SubTaskService subTaskService;

    // ========= Mutations ========= //

    /**
     * Creates a new SubTask.
     *
     * @param subTask the SubTaskDTO containing subtask data
     * @return the created SubTaskDTO
     */
    @MutationMapping
    public SubTaskDTO createSubTask(@Argument("subTask") SubTaskDTO subTask) {
        log.info("Creating subtask: {}", subTask);
        return subTaskService.createSubTask(subTask);
    }

    /**
     * Deletes a SubTask by ID.
     *
     * @param subTaskId the subtask ID
     * @return response indicating success/failure
     */
    @MutationMapping
    public ResponseDTO deleteSubTask(@Argument("subTaskId") String subTaskId) {
        log.info("Deleting subtask with ID: {}", subTaskId);
        return subTaskService.deleteSubTask(subTaskId);
    }

    /**
     * Assigns a member to a SubTask.
     *
     * @param taskId the task ID
     * @param memberId the member ID
     * @return updated SubTaskDTO
     */
    @MutationMapping
    public SubTaskDTO assignMemberToSubTask(@Argument("taskId") String taskId, @Argument("memberId") String memberId) {
        log.info("Assigning member {} to subtask {}", memberId, taskId);
        return subTaskService.assignMemberToSubTask(taskId, memberId);
    }

    /**
     * Unassigns a member from a SubTask.
     *
     * @param taskId the task ID
     * @param memberId the member ID
     * @return updated SubTaskDTO
     */
    @MutationMapping
    public SubTaskDTO unAssignMemberToSubTask(@Argument("taskId") String taskId, @Argument("memberId") String memberId) {
        log.info("Unassigning member {} from subtask {}", memberId, taskId);
        return subTaskService.unAssignedMemberFromTask(taskId, memberId);
    }

    /**
     * Changes the status of a SubTask.
     *
     * @param subTaskId the subtask ID
     * @param status the new status
     * @return updated SubTaskDTO
     */
    @MutationMapping
    public SubTaskDTO changeSubTaskStatus(@Argument("subTaskId") String subTaskId, @Argument("status") Status status) {
        log.info("Changing status of SubTask {} to {}", subTaskId, status);
        return subTaskService.changeSubTaskStatus(subTaskId, status);
    }

    // ========= Queries ========= //

    /**
     * Retrieves a SubTask by ID.
     *
     * @param subTaskId the subtask ID
     * @return SubTaskDTO
     */
    @QueryMapping
    public SubTaskDTO getSubTaskById(@Argument("subTaskId") String subTaskId) {
        log.info("Retrieving subtask with ID: {}", subTaskId);
        return subTaskService.getSubTaskById(subTaskId);
    }

    /**
     * Retrieves all SubTasks.
     *
     * @return list of SubTaskDTOs
     */
    @QueryMapping
    public List<SubTaskDTO> getAllSubTasks() {
        log.info("Retrieving all subtasks");
        return subTaskService.getAllSubTasks();
    }

    /**
     * Retrieves all SubTasks for a given Task.
     *
     * @param taskId the task ID
     * @return list of SubTaskDTOs
     */
    @QueryMapping
    public List<SubTaskDTO> getSubTasksByTaskId(@Argument String taskId) {
        log.info("Retrieving subtasks for Task ID: {}", taskId);
        return subTaskService.getSubTasksByTaskId(taskId);
    }

    /**
     * Retrieves all SubTasks for a given Project.
     *
     * @param projectId the project ID
     * @return list of SubTaskDTOs
     */
    @QueryMapping
    public List<SubTaskDTO> getSubTasksByProjectId(@Argument String projectId) {
        log.info("Retrieving subtasks for Project ID: {}", projectId);
        return subTaskService.getSubTasksByProjectId(projectId);
    }

    /**
     * Associates a SubTask with a Task.
     *
     * @param taskId the task ID
     * @param subTaskId the subtask ID
     * @return response indicating association success
     */
    @QueryMapping
    public ResponseDTO addSubTaskOnTask(@Argument("taskId") String taskId, @Argument("subTaskId") String subTaskId) {
        log.info("Associating SubTask {} with Task {}", subTaskId, taskId);
        return subTaskService.addSubTaskOnTask(taskId, subTaskId);
    }
    @QueryMapping
    public List<SubTaskDTO> getSubTasksAssignedToUser(@Argument("userId")String userId){
        return  subTaskService.getSubTasksAssignedToUser(userId);
    }
}
