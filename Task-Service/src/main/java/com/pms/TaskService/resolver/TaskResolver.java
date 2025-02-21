package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.TaskDTO;
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


    /* create task */
    @MutationMapping("createTask")
    public ResponseEntity<TaskDTO> createTask(@Argument("task") TaskDTO taskDTO){
        TaskDTO task = taskService.createTask(taskDTO);
        log.info(task.getTitle());
        return new ResponseEntity<>(task,  HttpStatus.CREATED);
    }

    //get All task
    @QueryMapping("getTaskById")
    public ResponseEntity<TaskDTO> getTaskById(@Argument String taskId){
        TaskDTO task = taskService.getTaskById(taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @MutationMapping("deleteTask")
    public ResponseEntity<Boolean> deleteTask(@Argument String taskId){
        boolean isDelete = taskService.deleteTask(taskId);
        return new ResponseEntity<>(isDelete, HttpStatus.OK);
    }

    @MutationMapping("updateTask")
    public ResponseEntity<TaskDTO> updateTask(@Argument TaskDTO taskDTO, @Argument String taskId){
        TaskDTO task = taskService.updateTask(taskDTO, taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    /* Add task on Epic */
    @MutationMapping("addTaskOnEpic")
    public ResponseDTO addTaskOnEpic(@Argument String epicId, @Argument String taskId){
        return taskService.addTaskOnEpic(epicId, taskId);
    }


    @QueryMapping("getAllTaskByProjectId")
    public List<TaskDTO> getAllTaskByProjectId(@Argument String projectId){
        return taskService.getAllTaskByProjectId(projectId);
    }


}
