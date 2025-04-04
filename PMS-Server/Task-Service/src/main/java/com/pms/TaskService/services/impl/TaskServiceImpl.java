package com.pms.TaskService.services.impl;

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
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /**
     * convert the Task entity into the TaskDTO
     * @param  task take the details
     * @return  TaskDTO
     */
    private TaskDTO convertToDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }

    /**
     * convert the task entity into the taskDTO
     * @param taskDTO take input
     * @return Task entity
     */
   private Task convertToEntity(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO,Task.class);
   }

    /**
     *
     * @param taskDTO takes input
     * @return TaskEvent
     */
   private TaskEvent generateTaskEvent(Task taskDTO) {
       return TaskEvent.builder()
               .entityId(taskDTO.getId())
               .title(taskDTO.getTitle())
               .assignees(taskDTO.getAssignees())
               .projectId(taskDTO.getProjectId())
               .eventType(EventType.TASK)
               .createdDate(taskDTO.getCreatedAt())
               .priority(taskDTO.getPriority())
               .deadline(taskDTO.getDeadLine())
               .event(EventType.TASK)
               .createdDate(taskDTO.getCreatedAt())
               .description(taskDTO.getDescription())
               .build();
   }

    /**
     *
     * @param taskId of the Task
     * @return Task Entity
     */
   private Task getTaskEntity(String taskId) {
       return taskRepository.findById(taskId).orElseThrow(()->
               new ResourceNotFound("Task not found :"+taskId));
   }

    @Override
    @Transactional
    public TaskDTO changeTaskStatus(String taskId, Status status) {
        Task task = getTaskEntity(taskId);
        task.setStatus(status);
        Task savedTask = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Override
   @Transactional
    public TaskDTO createTask(TaskDTO taskDTO, MultipartFile file) {

        Task task = convertToEntity(taskDTO) ;

        if (taskDTO.getEpicId() != null) {
            Epic epic = epicRepository.findById(taskDTO.getEpicId()).orElseThrow(()->new ResourceNotFound("Invalid Epic ID"));
        }

        String imageUrl = cloudinaryService.uploadImage(file);
        task.setImage(imageUrl);
        task.setCreatedAt(LocalDate.now());
        Task savedTask = taskRepository.save(task);

        // add the task into the epic if epic id present
        String epicId = taskDTO.getEpicId();
        String storyId = taskDTO.getStoryId();

        if (epicId != null) {
            // add the task within the epic
            addTaskOnEpic(epicId,savedTask);
        } else  if ( storyId != null){
            addTaskOnStory(storyId,savedTask);
        }else {
            // add the task within project directly
            projectFeignClient.addTaskToProject(task.getProjectId(),savedTask.getId());
        }

        TaskEvent taskEvent = generateTaskEvent(savedTask);
        // set the necessary details
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setNewStatus(savedTask.getStatus());
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setEvent(EventType.TASK);
        calendarEventProducer.sendTaskEvent(taskEvent);
        return convertToDTO(savedTask);
    }


    @Override
    public TaskDTO getTaskById(String taskId) {
        return  convertToDTO(getTaskEntity(taskId));
    }

    @Override
    @Transactional
    public ResponseDTO deleteTask(String taskId) {

        //  Retrieve the Task entity by its taskId
        Task task = getTaskEntity(taskId); // Assuming a method that retrieves Task by ID
        // Check if all subtasks are completed
        boolean allSubtasksCompleted = task.getSubTasks().stream()
                .allMatch(subTask -> subTask.getCompletionPercent() == 100 || subTask.getStatus() == Status.COMPLETED);

        // If all subtasks are completed, mark the task as ARCHIVED
        if (allSubtasksCompleted) {
            task.setStatus(Status.COMPLETED);
            taskRepository.save(task);
        } else {
            // If not all subtasks are completed, return an error response
            throw  new ResourceAlreadyExist("Task cannot be Deleted. Not all subtasks are completed.");
        }

        TaskEvent taskEvent = generateTaskEvent(task);
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAction(Actions.DELETED);
        taskEvent.setOldStatus(task.getStatus());
        taskEvent.setNewStatus(Status.COMPLETED);
        taskEvent.setDescription("Task Successfully deleted");

        calendarEventProducer.sendTaskEvent(taskEvent);
        //  Return a response indicating success
        return new ResponseDTO("Task has been archived or deleted successfully.");
    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDTO) {

        // Fetch the existing task from the database
        Task existingTask = getTaskEntity(taskDTO.getId());

        if(existingTask.getStatus() == Status.COMPLETED ||existingTask.getStatus()== Status.ARCHIVED )
            throw new ResourceNotFound("Task is Already Marked as Completed , Can't Updated");

        Task toBeModifiedTask =   modelMapper.map(taskDTO,Task.class);
        log.info(" assignees {}",existingTask.getAssignees());
        toBeModifiedTask.setAssignees(existingTask.getAssignees());
        toBeModifiedTask.setStory(existingTask.getStory());
        toBeModifiedTask.setEpic(existingTask.getEpic());
        toBeModifiedTask.setProjectId(existingTask.getProjectId());
        toBeModifiedTask.setSubTasks(existingTask.getSubTasks());

        toBeModifiedTask.setUpdatedAt(LocalDate.now());
        toBeModifiedTask.setCreatedAt(existingTask.getCreatedAt());

        Task modifiedTask =  taskRepository.save(toBeModifiedTask);


        TaskEvent taskEvent = generateTaskEvent(modifiedTask);

        taskEvent.setAction(Actions.UPDATED);
        taskEvent.setNewStatus(existingTask.getStatus());
        taskEvent.setOldStatus(modifiedTask.getStatus());
        taskEvent.setUpdatedDate(LocalDate.now());

        // sending the task-topic to the notification service
        taskEventProducer.sendTaskEvent(taskEvent);

        taskEvent.setEventType(EventType.CALENDER);
        // sending the calendar-topic to the activity tracker service
        calendarEventProducer.sendTaskEvent(taskEvent);

        return modelMapper.map(modifiedTask, TaskDTO.class);
    }


    @Override
    public List<TaskDTO> getAllTaskByProjectId(String projectId) {
        // Retrieve tasks by projectId using the custom repository method
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        // Convert each Task entity to TaskDTO and collect them into a list
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void addTaskOnEpic(String epicId, Task task) {

        Epic epic = epicRepository.findById(epicId).orElseThrow(()->
                new ResourceNotFound("Epic not found :"+ epicId));
        if (epic.getStatus() == Status.COMPLETED)
                throw  new ResourceNotFound("Task is Already Completed or Deleted ");
        epic.getTasks().add(task);
        task.setEpic(epic);
        // persist the changes into the db
        epicRepository.save(epic);
        taskRepository.save(task);

        ResponseDTO.builder()
                .message(" Task added  into the epic")
                .build();
    }

    private void addTaskOnStory(String storyId, Task savedTask) {
        Story story = storyRepository.findById(storyId).orElseThrow(()->
                new ResourceNotFound("Invalid Story Id: "+storyId));
        if (story.getStatus() == Status.COMPLETED)
            throw  new ResourceNotFound(" Story is already marked as completed or Deleted");
        story.getTasks().add(savedTask);
        savedTask.setStory(story);
        storyRepository.save(story);
        taskRepository.save(savedTask);
    }
    @Override
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .filter(task -> task.getStatus() != Status.COMPLETED)
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<TaskDTO> getTasksByUserId(String userId) {
        // Retrieve tasks where the userId is an assignee
        List<Task> tasks = taskRepository.findByAssigneesContains(userId);

        // Convert the list of Task entities to a list of TaskDTOs using ModelMapper
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByStatus(Status status,String epicId) {
        List<Task> tasks = taskRepository.findAllByStatusAndEpic_Id(status,epicId);
        return tasks.stream()
                .map(this::convertToDTO)
                .toList();
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
        Task task  = getTaskEntity(taskId);
        if (!task.getAssignees().contains(memberId)){
            throw  new ResourceNotFound("Member is not present at this task:"+ memberId);
        }
        task.getAssignees().remove(memberId);
        Task task1 = taskRepository.save(task);
        TaskEvent taskEvent = generateTaskEvent(task1);
        taskEvent.setAction(Actions.UNASSIGNED);
        taskEvent.setAssignees(Set.of(memberId));

        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(task1);
    }


}
