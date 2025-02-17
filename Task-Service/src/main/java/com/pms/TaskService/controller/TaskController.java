package com.pms.TaskService.controller;


import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.TaskServices;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TaskController {
    private final TaskServices taskServices;


    // create task
    @MutationMapping("createTask")
    public ResponseEntity<TaskDTO> createTask(@Argument("task") TaskDTO taskDTO){
        TaskDTO task = taskServices.createTask(taskDTO);
        return new ResponseEntity<>(task,  HttpStatus.CREATED);
    }

    //get All task
    @QueryMapping("getTaskById")
    public ResponseEntity<TaskDTO> getTaskById(@Argument String taskId){
        TaskDTO task = taskServices.getTaskById(taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @MutationMapping("deleteTask")
    public ResponseEntity<Boolean> deleteTask(@Argument String taskId){
        boolean isDelete = taskServices.deleteTask(taskId);
        return new ResponseEntity<>(isDelete, HttpStatus.OK);
    }

    @MutationMapping("updateTask")
    public ResponseEntity<TaskDTO> updateTask(@Argument TaskDTO taskDTO, @Argument String taskId){
        TaskDTO task = taskServices.updateTask(taskDTO, taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @QueryMapping("getAllTaskByProjectId")
    public ResponseEntity<List<TaskDTO>> getAllTaskByProjectId(@Argument String projectId){
        List<TaskDTO> taskDTO = taskServices.getAllTaskByProjectId(projectId);
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }



}
