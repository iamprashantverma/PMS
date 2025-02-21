package com.pms.TaskService.services;


import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.dto.SubTaskInputDTO;
import com.pms.TaskService.entities.SubTask;

public interface SubTaskService{

    public SubTaskDTO createSubTask(SubTaskInputDTO subtask);

    public ResponseDTO deleteSubTask(String subTaskId);

    public SubTask getSubTaskById(String subTaskId);

    public ResponseDTO addSubTaskOnTask(String taskId, String subTaskId);

}
