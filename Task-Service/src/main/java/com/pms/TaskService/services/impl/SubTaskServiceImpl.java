package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.dto.SubTaskInputDTO;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.SubTask;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.repository.SubTaskRepository;
import com.pms.TaskService.repository.TaskRepository;
import com.pms.TaskService.services.SubTaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubTaskServiceImpl implements SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    @Override
    public SubTaskDTO createSubTask(SubTaskInputDTO subtask) {
        SubTask subTask = modelMapper.map(subtask, SubTask.class);
        // TODO add creater id (current user)
//        task.setCreater(UserContextHolder.getCurrentUserId());
        SubTask savedTask = subTaskRepository.save(subTask);
        log.info("SubTask create with title {}",savedTask.getTitle());

        return modelMapper.map(savedTask, SubTaskDTO.class);
    }

    @Override
    public ResponseDTO deleteSubTask(String subTaskId) {
        return null;
    }

    @Override
    public SubTask getSubTaskById(String subTaskId) {
        return null;
    }

    @Override
    @Transactional
    public ResponseDTO addSubTaskOnTask(String taskId, String subTaskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFound("Task not found with id "+taskId));
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(()-> new ResourceNotFound("SubTask not found with id "+subTaskId));

        subTask.setParentTask(task);
        SubTask savedSubTask = subTaskRepository.save(subTask);
        task.getSubTasks().add(savedSubTask);
        taskRepository.save(task);
        return new ResponseDTO("Subtask mapped on task successfully");

    }
}
