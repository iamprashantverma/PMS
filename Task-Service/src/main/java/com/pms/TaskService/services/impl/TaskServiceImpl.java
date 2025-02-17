package com.pms.TaskService.services.impl;

import com.pms.TaskService.auth.UserContextHolder;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.repository.TaskRepository;
import com.pms.TaskService.services.TaskServices;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskServices {
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;


    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = modelMapper.map(taskDTO, Task.class);
        // TODO add creater id (current user)
        task.setCreaterId(UserContextHolder.getCurrentUserId());
        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskDTO.class);
    }

    @Override
    public TaskDTO getTaskById(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not exist with id: "+ taskId));
        return modelMapper.map(task, TaskDTO.class);
    }

    @Override
    public boolean deleteTask(String taskId) {
        if(!isExist(taskId)){
            throw new ResourceNotFound("Task not exist with id: "+ taskId);
        }
        taskRepository.deleteById(taskId);
        return true;

    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDTO, String taskId) {
        if(!isExist(taskId)){
            throw new ResourceNotFound("Task not exist with id: "+ taskId);
        }
        // TODO implement it
        return null;
    }

    @Override
    public List<TaskDTO> getAllTaskByProjectId(String projectId) {
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        List<TaskDTO> taskDTOS = tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
        return taskDTOS;

    }

    public boolean isExist(String taskId){
        return taskRepository.existsById(taskId);
    }
}
