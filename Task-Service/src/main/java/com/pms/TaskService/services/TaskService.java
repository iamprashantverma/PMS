package com.pms.TaskService.services;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.TaskDTO;

import java.util.List;

public interface TaskService {
    public TaskDTO createTask(TaskDTO taskDTO);

    TaskDTO getTaskById(String taskId);

    boolean deleteTask(String taskId);

    TaskDTO updateTask(TaskDTO taskDTO, String taskId);

    List<TaskDTO> getAllTaskByProjectId(String projectId);


    ResponseDTO addTaskOnEpic(String epicId, String taskId);
}
