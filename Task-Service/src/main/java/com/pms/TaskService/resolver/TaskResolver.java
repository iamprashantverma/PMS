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
    public TaskDTO createTask(@Argument("task") TaskDTO taskDTO){
        TaskDTO task = taskService.createTask(taskDTO);
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
    public ResponseEntity<TaskDTO> updateTask(@Argument("task") TaskDTO taskDTO, @Argument String taskId){
        TaskDTO task = taskService.updateTask(taskDTO, taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
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
    public List<TaskDTO> getTasksByStatus(@Argument("status")Status status) {
        return taskService.getTasksByStatus(status);
    }
}
