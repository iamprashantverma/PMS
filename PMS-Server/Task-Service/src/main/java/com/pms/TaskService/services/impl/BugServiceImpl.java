package com.pms.TaskService.services.impl;

import com.pms.TaskService.clients.ProjectFeignClient;
import com.pms.TaskService.dto.BugDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.Bug;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.Story;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.CalendarEventProducer;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.BugRepository;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.repository.StoryRepository;
import com.pms.TaskService.repository.TaskRepository;
import com.pms.TaskService.services.BugService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BugServiceImpl implements BugService {

    private final BugRepository bugRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final TaskEventProducer producer;
    private final EpicRepository epicRepository;
    private final StoryRepository storyRepository;
    private final CalendarEventProducer calendarEventProducer;
    private final ProjectFeignClient projectFeignClient;

    /**
     * Converts BugDTO to Bug entity.
     *
     * @param bugDTO the BugDTO to convert
     * @return the corresponding Bug entity
     */
    private Bug convertToEntity(BugDTO bugDTO) {
        return modelMapper.map(bugDTO, Bug.class);
    }

    /**
     * Converts Bug entity to BugDTO.
     *
     * @param bug the Bug entity to convert
     * @return the corresponding BugDTO
     */
    private BugDTO convertToDTO(Bug bug) {
        return modelMapper.map(bug, BugDTO.class);
    }

    /**
     * Generates a TaskEvent for Kafka messaging.
     *
     * @param bug the Bug entity
     * @return the generated TaskEvent
     */
    private TaskEvent generateTaskEvent(Bug bug) {
        return TaskEvent.builder()
                .entityId(bug.getId())
                .title(bug.getTitle())
                .projectId(bug.getEpic() == null ? bug.getProjectId():bug.getEpic().getProjectId())
                .priority(bug.getPriority())
                .eventType(EventType.BUG)
                .deadline(bug.getDeadLine())
                .createdDate(bug.getCreatedAt())
                .event(EventType.BUG)
                .description(bug.getDescription())
                .build();
    }

    @Override
    @Transactional
    public BugDTO createBug(BugDTO bugDTO) {

        // Convert DTO to Entity
        Bug bug = convertToEntity(bugDTO);

        // Associate Bug with the Epic or Story
        String epicId = bugDTO.getEpicId();
        String storyId = bugDTO.getStoryId();
        String taskId =  bugDTO.getTaskId();

        if (epicId != null) {
            Epic epic = epicRepository.findById(epicId).orElseThrow(()->
                    new ResourceNotFound("Invalid Epic Id"));
            bug.setEpic(epic);
        } else if (storyId != null) {
            Story story = storyRepository.findById(storyId).orElseThrow(()->
                    new ResourceNotFound("Invalid storyId: "+ storyId));
            bug.setStory(story);
        } else if (taskId != null) {
            Task task1 = taskRepository.findById(taskId).orElseThrow(()->
                    new ResourceNotFound("Invalid Task iD "));
        }
        bug.setCreatedAt(LocalDate.now());
        // Save the Bug
        Bug savedBug = bugRepository.save(bug);

        if (taskId == null && epicId == null && storyId == null) {
            // add this bug directly to the project
            log.info("{}",savedBug);
            projectFeignClient.addBugToProject(savedBug.getProjectId(),savedBug.getId());
        }
        // Publish the Bug Creation Event.
        TaskEvent taskEvent =  generateTaskEvent(savedBug);
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setEventType(EventType.CALENDER);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedBug);
    }

    @Override
    @Transactional
    public ResponseDTO deleteBug(String bugId) {

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));

        Status oldStatus = bug.getStatus();
        bug.setStatus(Status.COMPLETED);
        // Soft delete the Bug
        bugRepository.save(bug);

        // Publish the Bug Deletion Event
        TaskEvent taskEvent = generateTaskEvent(bug);

        taskEvent.setOldStatus(oldStatus);
        taskEvent.setAction(Actions.DELETED);

        taskEvent.setNewStatus(Status.COMPLETED);
        producer.sendTaskEvent(taskEvent);
        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return new ResponseDTO("Bug deleted successfully");
    }

    @Override
    public BugDTO getBugById(String bugId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));
        return convertToDTO(bug);
    }

    @Override
    public BugDTO updateBug(BugDTO bugDTO) {

        String id = bugDTO.getId();
        Bug toBeModified = convertToEntity(bugDTO);

        Bug existingBug = bugRepository.findById(id).orElseThrow(()->
                new ResourceNotFound("Invalid Bug id"));

        Status oldStatus = existingBug.getStatus();
        Priority oldPriority = existingBug.getPriority() ;

        toBeModified.setUpdatedAt(LocalDate.now());
        toBeModified.setCreatedAt(existingBug.getCreatedAt());
        toBeModified.setId(id);
        toBeModified.setCreatedAt(existingBug.getCreatedAt());
        toBeModified.setUpdatedAt(LocalDate.now());
        toBeModified.setAssignees(existingBug.getAssignees());
        toBeModified.setStory(existingBug.getStory());
        toBeModified.setEpic(existingBug.getEpic());

        Bug savedBug =  bugRepository.save(toBeModified);

        TaskEvent taskEvent = generateTaskEvent(toBeModified);
        taskEvent.setCreatedDate(savedBug.getCreatedAt());
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(bugDTO.getStatus());
        taskEvent.setDeadline(bugDTO.getDeadline());
        taskEvent.setAssignees(existingBug.getAssignees());
        taskEvent.setAction(Actions.UPDATED);

        producer.sendTaskEvent(taskEvent);

        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(toBeModified);
    }

    @Override
    @Transactional
    public ResponseDTO assignBugToUser(String bugId, String userId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));
        if (bug.getStatus() == Status.COMPLETED)
                throw  new ResourceNotFound(" Bug is already marks as completed, can't assign new member");
        // Add the user to the list of assignees
        bug.getAssignees().add(userId);
        bugRepository.save(bug);

        // Publish the Bug Update Event
        TaskEvent taskEvent  = generateTaskEvent(bug);
        taskEvent.setAssignees(Set.of(userId));
        taskEvent.setAction(Actions.ASSIGNED);
        producer.sendTaskEvent(taskEvent);

        return new ResponseDTO("Bug assigned to user successfully");
    }

    @Override
    @Transactional
    public ResponseDTO changeBugStatus(String bugId, Status status) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));
        Status oldStatus = bug.getStatus();

        bug.setStatus(status);
        bugRepository.save(bug);

        // Publish the Bug Status Change Event
        TaskEvent taskEvent = generateTaskEvent(bug) ;
        taskEvent.setNewStatus(status);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setAction(Actions.STATUS_CHANGED);
        taskEvent.setAssignees(bug.getAssignees());
        producer.sendTaskEvent(taskEvent);

        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return new ResponseDTO("Bug status updated successfully");
    }

    @Override
    public List<BugDTO> getBugsByProjectId(String projectId) {
        List<Bug> bugs = bugRepository.findByProjectId(projectId);
        return bugs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BugDTO> getBugsByUserId(String userId) {
        List<Bug> bugs = bugRepository.findByAssignees(userId);
        return bugs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
