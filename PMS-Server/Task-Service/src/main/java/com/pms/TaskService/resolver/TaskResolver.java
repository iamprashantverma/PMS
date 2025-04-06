package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskResolver {

    private final TaskService taskService;

    /**
     * Create the task
     * @param taskDTO contains details of the task
     * @return  TaskDTO
     */
    @MutationMapping
    public TaskDTO createTask(@Argument("task") TaskDTO taskDTO, @Argument("image")MultipartFile file){
        TaskDTO task = taskService.createTask(taskDTO,file);
        log.info(task.getTitle());
        return task;
    }

    /**
     *
     * @param taskId of this id we want the task
     * @return TaskDTO
     */
    @QueryMapping
    public TaskDTO getTaskById(@Argument("taskId") String taskId){
        return taskService.getTaskById(taskId);
    }

    @MutationMapping
    public ResponseDTO deleteTask(@Argument("taskId") String taskId){
        return taskService.deleteTask(taskId);
    }

    @MutationMapping
    public TaskDTO updateTask(@Argument("task") TaskDTO taskDTO){
        TaskDTO task = taskService.updateTask(taskDTO);
        log.info("updated Task Entity {}",task);
        return task;
    }

    @QueryMapping
    public List<TaskDTO> getAllTaskByProjectId(@Argument("projectId") String projectId){
        return taskService.getAllTaskByProjectId(projectId);
    }

    /**
     *  fetch all the tasks
     * @return List<TaskDTO>
     */
    @QueryMapping
    public List<TaskDTO> getAllTasks(){
        return taskService.getAllTasks();
    }

    /**
     * get all the task which is allocated to a user
     *
     * @param userId id of the user to  fetch he all tasks
     * @return  list of TaskDTO
     */
    @QueryMapping
    public List<TaskDTO> getTasksByUserId(@Argument("userId") String userId){
        return taskService.getTasksByUserId(userId);
    }

    /**
     *
     * @param status of this status get all task
     * @return list of taskDTO
     */

    @QueryMapping
    public List<TaskDTO> getTasksByStatusAndEpic(@Argument("status")Status status,@Argument("epicId")String epicId) {
        return taskService.getTasksByStatus(status,epicId);
    }
    /**
     * Assigns a member to the specified task using GraphQL mutation.
     *
     * @param taskId   the ID of the task to which the member should be assigned
     * @param memberId the ID of the member to be assigned to the task
     * @return the updated TaskDTO after assigning the member
     */
    @MutationMapping
    public TaskDTO assignMemberToTask(@Argument("taskId") String taskId, @Argument("memberId") String memberId) {
        return taskService.assignMemberToTask(taskId, memberId);
    }

    /**
     * Unassigns a member from the specified task using GraphQL mutation.
     *
     * @param taskId   the ID of the task from which the member should be removed
     * @param memberId the ID of the member to be unassigned from the task
     * @return the updated TaskDTO after unassigning the member
     */
    @MutationMapping
    public TaskDTO unAssignMemberToTask(@Argument("taskId") String taskId, @Argument("memberId") String memberId) {
        return taskService.unAssignedMemberFromTask(taskId, memberId);
    }

    @MutationMapping
    public TaskDTO changeTaskStatus(@Argument("taskId") String taskId, @Argument("status") Status status) {
        return taskService.changeTaskStatus(taskId,status);
    }

    @MutationMapping
    public TaskDTO assignNewParent(@Argument("parentId")String parentId,@Argument("taskId")String taskId,@Argument("parentType")String parent){
        return taskService.assignNewParent(parentId,taskId,parent);
    }
    @QueryMapping
    public List<TaskDTO> getTaskByEpicId(@Argument("epicId")String epicId){
        return taskService.getTaskByEpicId(epicId);
    }
}
