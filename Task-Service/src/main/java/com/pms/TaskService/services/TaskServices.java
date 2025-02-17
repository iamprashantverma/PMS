package com.pms.TaskService.services;

import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;

import java.util.List;

public interface TaskServices {
    public TaskDTO createTask(TaskDTO taskDTO);



    TaskDTO getTaskById(String taskId);

    boolean deleteTask(String taskId);

    TaskDTO updateTask(TaskDTO taskDTO, String taskId);

    List<TaskDTO> getAllTaskByProjectId(String projectId);


}
