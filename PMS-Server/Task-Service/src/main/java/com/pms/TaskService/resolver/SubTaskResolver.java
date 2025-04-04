package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.SubTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubTaskResolver {

    private final SubTaskService subTaskService;

    /**
     * Creates a new subtask.
     *
     * @param subTask the input subtask data as SubTaskInputDTO (mapped to SubTaskDTO)
     * @return the created subtask as a SubTaskDTO
     */
    @MutationMapping
    public SubTaskDTO createSubTask(@Argument("subTask") SubTaskDTO subTask) {
        log.info("Creating subtask: {}", subTask);
        return subTaskService.createSubTask(subTask);
    }

    /**
     * Deletes a subtask by its ID.
     *
     * @param subTaskId the ID of the subtask to delete
     * @return a ResponseDTO indicating the result of the deletion
     */
    @MutationMapping
    public ResponseDTO deleteSubTask(@Argument("subTaskId") String subTaskId) {
        log.info("Deleting subtask with id: {}", subTaskId);
        return subTaskService.deleteSubTask(subTaskId);
    }

    /**
     * Retrieves a subtask by its ID.
     *
     * @param subTaskId the ID of the subtask to retrieve
     * @return the SubTaskDTO corresponding to the provided ID
     */
    @QueryMapping
    public SubTaskDTO getSubTaskById(@Argument("subTaskId") String subTaskId) {
        log.info("Retrieving subtask with id: {}", subTaskId);
        return subTaskService.getSubTaskById(subTaskId);
    }

    /**
     * Retrieves all subtasks.
     *
     * @return a list of all SubTaskDTOs
     */
    @QueryMapping
    public List<SubTaskDTO> getAllSubTasks() {
        log.info("Retrieving all subtasks");
        return subTaskService.getAllSubTasks();
    }

    /**
     * Associates an existing subtask with a task.
     *
     * @param taskId    the ID of the task to which the subtask should be associated
     * @param subTaskId the ID of the subtask to associate with the task
     * @return the updated SubTaskDTO after association
     */
    @QueryMapping
    public ResponseDTO addSubTaskOnTask(@Argument("taskId") String taskId, @Argument("subTaskId") String subTaskId) {
        log.info("Associating subtask {} with task {}", subTaskId, taskId);
        return subTaskService.addSubTaskOnTask(taskId, subTaskId);
    }

    /**
     *
     * @param taskId the id of task to get their all  subtask retrieves
     * @return List<SubTaskDTO>
     */
    @QueryMapping
    public List<SubTaskDTO> getSubTasksByTaskId(@Argument String taskId){
        return subTaskService.getSubTasksByTaskId(taskId);
    }

    /**
     * Assigns a member to the specified SubTask using GraphQL mutation.
     *
     * @param taskId   the ID of the task to which the member should be assigned
     * @param memberId the ID of the member to be assigned to the SubTask
     * @return the updated SubTaskDTO after assigning the member
     */
    @MutationMapping
    public SubTaskDTO assignMemberToSubTask(@Argument("taskId") String taskId, @Argument("memberId") String memberId) {
        return subTaskService.assignMemberToSubTask(taskId, memberId);
    }

    /**
     * Unassigns a member from the specified SubTask using GraphQL mutation.
     *
     * @param taskId   the ID of the task from which the member should be removed
     * @param memberId the ID of the member to be unassigned from the SubTask
     * @return the updated SubTaskDTO after unassigning the member
     */
    @MutationMapping
    public SubTaskDTO unAssignMemberToSubTask(@Argument("taskId") String taskId, @Argument("memberId") String memberId) {
        return subTaskService.unAssignedMemberFromTask(taskId,memberId);
    }

    @MutationMapping
    public SubTaskDTO changeStatus(@Argument("subTaskId")String subTaskId, @Argument("status")Status status) {
        return subTaskService.changeSubTaskStatus(subTaskId,status);
    }
}
