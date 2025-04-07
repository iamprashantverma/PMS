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
import com.pms.TaskService.producer.CalendarEventProducer;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubTaskServiceImpl implements SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final TaskEventProducer producer;
    private final CalendarEventProducer calendarEventProducer;

    private SubTask convertToEntity(SubTaskDTO subTaskDTO) {
        return modelMapper.map(subTaskDTO, SubTask.class);
    }

    private SubTaskDTO convertToDTO(SubTask subTask) {
        return modelMapper.map(subTask, SubTaskDTO.class);
    }

    private TaskEvent generateTaskEvent(SubTask subTask) {
        return TaskEvent.builder()
                .entityId(subTask.getId())
                .title(subTask.getTitle())
                .eventType(EventType.SUBTASK)
                .deadline(subTask.getDeadLine())
                .createdDate(subTask.getCreatedAt())
                .newStatus(subTask.getStatus())
                .assignees(subTask.getAssignees())
                .event(EventType.SUBTASK)
                .build();
    }

    @Override
    @Transactional
    public SubTaskDTO createSubTask(SubTaskDTO subTaskDTO) {
        SubTask subTask = convertToEntity(subTaskDTO);

        Task task = taskRepository.findById(subTaskDTO.getTaskId())
                .orElseThrow(() -> new ResourceNotFound("Task not found: " + subTaskDTO.getTaskId()));

        if (task.getStatus() == Status.COMPLETED || task.getStatus() == Status.ARCHIVED) {
            throw new ResourceNotFound("Parent Task is already marked as completed. Cannot create new subtask.");
        }

        subTask.setParentTask(task);

        SubTask savedSubTask = subTaskRepository.save(subTask);
        log.info("{}", savedSubTask);

        task.getSubTasks().add(savedSubTask);
        taskRepository.save(task);
        log.info("{}", task);

        TaskEvent taskEvent = generateTaskEvent(savedSubTask);
        taskEvent.setNewStatus(savedSubTask.getStatus());
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setEventType(EventType.CALENDER);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedSubTask);
    }

    @Override
    @Transactional
    public ResponseDTO deleteSubTask(String subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFound("SubTask not found: " + subTaskId));

        Status oldStatus = subTask.getStatus();
        subTask.setStatus(Status.COMPLETED);
        subTaskRepository.save(subTask);

        TaskEvent taskEvent = generateTaskEvent(subTask);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(Status.COMPLETED);
        producer.sendTaskEvent(taskEvent);

        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAction(Actions.DELETED);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return new ResponseDTO("SubTask archived successfully");
    }

    @Override
    public SubTaskDTO getSubTaskById(String subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFound("SubTask not found: " + subTaskId));

        return convertToDTO(subTask);
    }

    @Override
    @Transactional
    public ResponseDTO addSubTaskOnTask(String taskId, String subTaskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found: " + taskId));

        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFound("SubTask not found: " + subTaskId));

        subTask.setParentTask(task);
        subTaskRepository.save(subTask);

        task.getSubTasks().add(subTask);
        taskRepository.save(task);

        return new ResponseDTO("SubTask successfully associated with Task");
    }

    @Override
    public List<SubTaskDTO> getAllSubTasks() {
        return subTaskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubTaskDTO> getSubTasksByTaskId(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found: " + taskId));

        return task.getSubTasks().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubTaskDTO assignMemberToSubTask(String taskId, String memberId) {
        SubTask subTask = subTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Invalid Task Id: " + taskId));

        subTask.getAssignees().add(memberId);
        SubTask savedSubTask = subTaskRepository.save(subTask);

        TaskEvent taskEvent = generateTaskEvent(savedSubTask);
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAssignees(Set.of(memberId));
        producer.sendTaskEvent(taskEvent);

        return convertToDTO(savedSubTask);
    }

    @Override
    public SubTaskDTO unAssignedMemberFromTask(String taskId, String memberId) {
        SubTask subTask = subTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Invalid Task Id: " + taskId));

        if (!subTask.getAssignees().contains(memberId)) {
            throw new ResourceNotFound("Member not found: " + memberId);
        }

        subTask.getAssignees().remove(memberId);
        SubTask savedSubTask = subTaskRepository.save(subTask);

        TaskEvent taskEvent = generateTaskEvent(savedSubTask);
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAction(Actions.UNASSIGNED);
        producer.sendTaskEvent(taskEvent);

        return convertToDTO(savedSubTask);
    }

    @Override
    public List<SubTaskDTO> getSubTasksByProjectId(String projectId) {
        return subTaskRepository.findAllByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubTaskDTO> getSubTasksAssignedToUser(String userId) {
        List<SubTask> subTasks  = subTaskRepository.findAllSubTasksByAssignee(userId);
        return subTasks.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public SubTaskDTO changeSubTaskStatus(String subTaskId, Status status) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFound("Invalid subTask Id: " + subTaskId));

        subTask.setStatus(status);
        SubTask savedTask = subTaskRepository.save(subTask);
        return convertToDTO(savedTask);
    }
}
