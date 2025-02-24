package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.event.EventType;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.repository.TaskRepository;
import com.pms.TaskService.services.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final EpicRepository epicRepository;
    private final TaskEventProducer taskEventProducer;

    /* create the new  Task Event of type TASK_CREATED_EVENT */
    public static TaskEvent getTaskCreatedEvent(Task task) {
        return TaskEvent.builder()
                .eventType(EventType.TASK_CREATED)
                .title(task.getTitle())
                .priority(task.getPriority())
                .taskId(task.getId())
                .description(task.getDescription())
                .newStatus(task.getStatus())
                .projectId(task.getProject())
                .assignees(task.getAssignees())
                .createdDate(task.getCreatedDate())
                .build();
    }

    /* creating the TaskEvent of type TASK_COMPLETED_EVENT */

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {

        /* converted the taskDTO into the task entity */
        Task task = modelMapper.map(taskDTO, Task.class);
        /* saved the new task into the DB */
        Task savedTask = taskRepository.save(task);

        log.info(savedTask.getTitle());

        /* creating the taskEvent  */
        TaskEvent event = getTaskCreatedEvent(task);
        /* send the event */
        taskEventProducer.sendTaskEvent(event);

        /* return the saved task */
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
    public List<TaskDTO> getAllTaskByProjectId(String project) {
        List<Task> tasks = taskRepository.findAllByProject(project);
        List<TaskDTO> taskDTOS = tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
        return taskDTOS;

    }

    /* Add task on epic */
    @Override
    public ResponseDTO addTaskOnEpic(String epicId, String taskId) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(()-> new ResourceNotFound("Epic not found with id "+epicId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFound("Story not found with id "+taskId));

        task.setEpic(epic);
        Task savedTask = taskRepository.save(task);
        epic.getTasks().add(savedTask);
        epicRepository.save(epic);
        return new ResponseDTO("Story added on Epic successfully");
    }

    public boolean isExist(String taskId){
        return taskRepository.existsById(taskId);
    }
}
