package com.pms.TaskService.services.impl;

import com.pms.TaskService.auth.UserContextHolder;
import com.pms.TaskService.clients.ProjectFeignClient;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.TaskDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.Story;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.exceptions.ResourceAlreadyExist;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.CalendarEventProducer;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.repository.StoryRepository;
import com.pms.TaskService.repository.TaskRepository;
import com.pms.TaskService.services.CloudinaryService;
import com.pms.TaskService.services.TaskService;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final EpicRepository epicRepository;
    private final TaskEventProducer taskEventProducer;
    private final CalendarEventProducer calendarEventProducer;
    private final ProjectFeignClient projectFeignClient;
    private final StoryRepository storyRepository;
    private final CloudinaryService cloudinaryService;

    private String getCurrentUserId(){
        return UserContextHolder.getCurrentUserId();
    }


    private TaskDTO convertToDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }

    private Task convertToEntity(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    private TaskEvent generateTaskEvent(Task task) {
        return TaskEvent.builder()
                .entityId(task.getId())
                .title(task.getTitle())
                .assignees(task.getAssignees())
                .projectId(task.getProjectId())
                .eventType(EventType.TASK)
                .createdDate(task.getCreatedAt())
                .priority(task.getPriority())
                .deadline(task.getDeadLine())
                .updatedBy(getCurrentUserId())
                .updatedDate(task.getUpdatedAt())
                .description(task.getDescription())
                .build();
    }

    private Task getTaskEntity(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found: " + taskId));
    }

    // Service Methods
    @Override
    public List<TaskDTO> getTaskByEpicId(String epicId) {
        return taskRepository.findAllByEpic_Id(epicId)
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksAssignedToUser(String userId) {
        List<Task> tasks = taskRepository.findAllByAssignee(userId);
        return tasks.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public TaskDTO assignNewParent(String parentId, String taskId, String parent) {
        Task task = getTaskEntity(taskId);

        switch (parent) {
            case "project" -> task.setProjectId(parentId);
            case "epic" -> task.setEpic(epicRepository.findById(parentId).orElse(null));
            case "story" -> task.setStory(storyRepository.findById(parentId).orElse(null));
        }

        return convertToDTO(taskRepository.save(task));
    }


    @Override
    @Transactional
    public TaskDTO changeTaskStatus(String taskId, Status status) {
        Task task = getTaskEntity(taskId);
        Status oldStatus = task.getStatus();
        task.setStatus(status);

        if(status == Status.TODO) {
            task.setCompletionPercent(0L);
        } else if(status == Status.IN_PLANNED) {
            task.setCompletionPercent(10L);
        } else if (status == Status.IN_PROGRESS) {
            task.setCompletionPercent(60L);
        } else if(status == Status.IN_QA) {
            task.setCompletionPercent(90L);
        } else if (status == Status.COMPLETED || status == Status.ARCHIVED) {
            task.setCompletionPercent(100L);
        }

        Task savedTask = taskRepository.save(task);
        TaskEvent taskEvent = generateTaskEvent(savedTask);

        log.info("task Entity,{}",savedTask.getUpdatedAt());
        taskEvent.setNewStatus(status);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setAction(Actions.STATUS_CHANGED);

        taskEventProducer.sendTaskEvent(taskEvent);
        return convertToDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO, MultipartFile file) {
        Task task = convertToEntity(taskDTO);

        if (taskDTO.getEpicId() != null) {
            epicRepository.findById(taskDTO.getEpicId())
                    .orElseThrow(() -> new ResourceNotFound("Invalid Epic ID"));
        }

        String imageUrl = cloudinaryService.uploadImage(file);
        task.setImage(imageUrl);

        Task savedTask = taskRepository.save(task);
        if (taskDTO.getEpicId() != null) {
            addTaskOnEpic(taskDTO.getEpicId(), savedTask);
        } else if (taskDTO.getStoryId() != null) {
            addTaskOnStory(taskDTO.getStoryId(), savedTask);
        } else {
            projectFeignClient.addTaskToProject(task.getProjectId(), savedTask.getId());
        }

        TaskEvent taskEvent = generateTaskEvent(savedTask);
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setNewStatus(savedTask.getStatus());
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setEvent(EventType.TASK);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedTask);
    }

    @Override
    public TaskDTO getTaskById(String taskId) {
        Task task = getTaskEntity(taskId);
        log.info("{}", task.getSubTasks());
        return convertToDTO(task);
    }

    @Override
    @Transactional
    public ResponseDTO deleteTask(String taskId) {
        Task task = getTaskEntity(taskId);

        boolean allSubtasksCompleted = task.getSubTasks().stream()
                .allMatch(subTask -> subTask.getCompletionPercent() == 100 || subTask.getStatus() == Status.COMPLETED);

        if (!allSubtasksCompleted) {
            throw new ResourceAlreadyExist("Task cannot be deleted. Not all subtasks are completed.");
        }

        task.setStatus(Status.COMPLETED);
        task.setCompletionPercent(100L);
        taskRepository.save(task);

        TaskEvent taskEvent = generateTaskEvent(task);
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAction(Actions.DELETED);
        taskEvent.setOldStatus(task.getStatus());
        taskEvent.setNewStatus(Status.COMPLETED);
        taskEvent.setDescription("Task Successfully deleted");

        calendarEventProducer.sendTaskEvent(taskEvent);

        return new ResponseDTO("Task has been archived or deleted successfully.");
    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDTO) {
        Task existingTask = getTaskEntity(taskDTO.getId());

        if (existingTask.getStatus() == Status.COMPLETED || existingTask.getStatus() == Status.ARCHIVED) {
            throw new ResourceNotFound("Task is already marked as completed. Cannot update.");
        }

        Task toBeModifiedTask = modelMapper.map(taskDTO, Task.class);

        toBeModifiedTask.setAssignees(existingTask.getAssignees());
        toBeModifiedTask.setStory(existingTask.getStory());
        toBeModifiedTask.setEpic(existingTask.getEpic());
        toBeModifiedTask.setProjectId(existingTask.getProjectId());
        toBeModifiedTask.setSubTasks(existingTask.getSubTasks());
        toBeModifiedTask.setCreatedAt(existingTask.getCreatedAt());
        toBeModifiedTask.setUpdatedAt(LocalDate.now());

        Task modifiedTask = taskRepository.save(toBeModifiedTask);

        TaskEvent taskEvent = generateTaskEvent(modifiedTask);
        taskEvent.setAction(Actions.UPDATED);
        taskEvent.setOldStatus(existingTask.getStatus());
        taskEvent.setNewStatus(modifiedTask.getStatus());
        taskEvent.setUpdatedDate(LocalDate.now());

        taskEventProducer.sendTaskEvent(taskEvent);
        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(modifiedTask);
    }

    @Override
    public List<TaskDTO> getAllTaskByProjectId(String projectId) {
        return taskRepository.findByProjectId(projectId)
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void addTaskOnEpic(String epicId, Task task) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found: " + epicId));

        if (epic.getStatus() == Status.COMPLETED) {
            throw new ResourceNotFound("Epic is already completed or deleted.");
        }

        epic.getTasks().add(task);
        task.setEpic(epic);
        epicRepository.save(epic);
        taskRepository.save(task);
    }

    private void addTaskOnStory(String storyId, Task task) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFound("Invalid Story ID: " + storyId));

        if (story.getStatus() == Status.COMPLETED) {
            throw new ResourceNotFound("Story is already marked as completed or deleted.");
        }

        story.getTasks().add(task);
        task.setStory(story);
        storyRepository.save(story);
        taskRepository.save(task);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() != Status.COMPLETED)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByUserId(String userId) {
        return taskRepository.findByAssigneesContains(userId)
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByStatus(Status status, String epicId) {
        return taskRepository.findAllByStatusAndEpic_Id(status, epicId)
                .stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO assignMemberToTask(String taskId, String memberId) {

        Task task = getTaskEntity(taskId);
        task.getAssignees().add(memberId);
        Task savedTask = taskRepository.save(task);

        TaskEvent taskEvent = generateTaskEvent(savedTask);
        taskEvent.setAction(Actions.ASSIGNED);
        taskEvent.setEventType(EventType.TASK);
        taskEvent.setAssignees(Set.of(memberId));
        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedTask);
    }

    @Override
    public TaskDTO unAssignedMemberFromTask(String taskId, String memberId) {
        Task task = getTaskEntity(taskId);

        if (!task.getAssignees().contains(memberId)) {
            throw new ResourceNotFound("Member is not assigned to this task: " + memberId);
        }

        task.getAssignees().remove(memberId);
        Task updatedTask = taskRepository.save(task);

        TaskEvent taskEvent = generateTaskEvent(updatedTask);
        taskEvent.setAction(Actions.UNASSIGNED);
        taskEvent.setAssignees(Set.of(memberId));
        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(updatedTask);
    }
}
