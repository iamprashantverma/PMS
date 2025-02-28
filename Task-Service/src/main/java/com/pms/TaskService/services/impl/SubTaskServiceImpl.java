package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.entities.SubTask;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.SubTaskRepository;
import com.pms.TaskService.repository.TaskRepository;
import com.pms.TaskService.services.SubTaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubTaskServiceImpl implements SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final TaskEventProducer producer;

    /**
     * Converts SubTaskDTO to SubTask entity.
     *
     * @param subTaskDTO the SubTaskDTO to convert
     * @return the corresponding SubTask entity
     */
    private SubTask convertToEntity(SubTaskDTO subTaskDTO) {
        return modelMapper.map(subTaskDTO, SubTask.class);
    }

    /**
     * Converts SubTask entity to SubTaskDTO.
     *
     * @param subTask the SubTask entity to convert
     * @return the corresponding SubTaskDTO
     */
    private SubTaskDTO convertToDTO(SubTask subTask) {
        return modelMapper.map(subTask, SubTaskDTO.class);
    }

    /**
     * Generates a TaskEvent for Kafka messaging.
     *
     * @param subTask the SubTask entity
     * @return the generated TaskEvent
     */
    private TaskEvent generateTaskEvent(SubTask subTask) {
        return TaskEvent.builder()
                .entityId(subTask.getId())
                .title(subTask.getTitle())
                .projectId(subTask.getParentTask().getEpic().getProjectId())
                .eventType(EventType.SUBTASK)
                .build();
    }

    /**
     * Creates a new subtask and associates it with a task.
     *
     * @param subTaskDTO the subtask data transfer object to create
     * @return the created SubTaskDTO
     */
    @Override
    @Transactional
    public SubTaskDTO createSubTask(SubTaskDTO subTaskDTO) {

        // Convert DTO to Entity
        SubTask subTask = convertToEntity(subTaskDTO);

        // Check if the related Task exists
        Task task = taskRepository.findById(subTaskDTO.getParentTaskId())
                .orElseThrow(() -> new ResourceNotFound("Task not found: " + subTaskDTO.getParentTaskId()));

        // Associate the subtask with the parent task
        subTask.setParentTask(task);

        // Save the SubTask
        SubTask savedSubTask = subTaskRepository.save(subTask);

        // Publish the SubTask Creation Event.
        TaskEvent taskEvent = generateTaskEvent(savedSubTask);
        taskEvent.setAction(Actions.CREATED);

        producer.sendTaskEvent(taskEvent);

        return convertToDTO(savedSubTask);
    }

    /**
     * Deletes a subtask by setting its status to ARCHIVED (soft delete).
     *
     * @param subTaskId the ID of the subtask to delete
     * @return a ResponseDTO with the result of the deletion
     */
    @Override
    @Transactional
    public ResponseDTO deleteSubTask(String subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFound("SubTask not found: " + subTaskId));

        Status oldStatus = subTask.getStatus();
        // Soft delete the SubTask by setting its status to ARCHIVED
        subTask.setStatus(Status.ARCHIVED);
        subTaskRepository.save(subTask);

        // Publish the SubTask Deletion Event
        TaskEvent taskEvent = generateTaskEvent(subTask);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(Status.ARCHIVED);

        producer.sendTaskEvent(taskEvent);

        return new ResponseDTO("SubTask archived successfully");
    }

    /**
     * Retrieves a subtask by its ID.
     *
     * @param subTaskId the ID of the subtask
     * @return the SubTaskDTO
     */
    @Override
    public SubTaskDTO getSubTaskById(String subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFound("SubTask not found: " + subTaskId));
        return convertToDTO(subTask);
    }

    /**
     * Associates an existing subtask with a task.
     *
     * @param taskId    the ID of the task
     * @param subTaskId the ID of the subtask
     * @return the response result of the operation
     */
    @Override
    @Transactional
    public ResponseDTO addSubTaskOnTask(String taskId, String subTaskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found: " + taskId));

        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFound("SubTask not found: " + subTaskId));

        // Associate the subtask with the task
        subTask.setParentTask(task);
        subTaskRepository.save(subTask);

        // Associate the task with subtask
        task.getSubTasks().add(subTask);
        taskRepository.save(task);


        return new ResponseDTO("SubTask successfully associated with Task");
    }

    /**
     * Retrieves all the subtasks.
     *
     * @return a list of all SubTaskDTOs
     */
    @Override
    public List<SubTaskDTO> getAllSubTasks() {
        List<SubTask> subTasks = subTaskRepository.findAll();
        return subTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all the subtasks related to a specific task.
     *
     * @param taskId the ID of the task
     * @return a list of SubTaskDTOs
     */
    @Override
    public List<SubTaskDTO> getSubTasksByTaskId(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found: " + taskId));

        List<SubTask> subTasks = task.getSubTasks();
        return subTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
