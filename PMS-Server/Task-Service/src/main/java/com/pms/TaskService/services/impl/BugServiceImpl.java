package com.pms.TaskService.services.impl;

import com.pms.TaskService.auth.UserContextHolder;
import com.pms.TaskService.clients.ProjectFeignClient;
import com.pms.TaskService.dto.BugDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.*;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.CalendarEventProducer;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.*;
import com.pms.TaskService.services.BugService;
import com.pms.TaskService.services.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final CloudinaryService cloudinaryService;

    private String getCurrentUserId() {
        return UserContextHolder.getCurrentUserId();
    }

    // Convert DTO to Entity
    private Bug convertToEntity(BugDTO bugDTO) {
        return modelMapper.map(bugDTO, Bug.class);
    }

    // Convert Entity to DTO
    private BugDTO convertToDTO(Bug bug) {
        return modelMapper.map(bug, BugDTO.class);
    }

    // Create a TaskEvent for bug
    private TaskEvent generateTaskEvent(Bug bug) {
        return TaskEvent.builder()
                .entityId(bug.getId())
                .title(bug.getTitle())
                .projectId(bug.getEpic() == null ? bug.getProjectId() : bug.getEpic().getProjectId())
                .priority(bug.getPriority())
                .eventType(EventType.BUG)
                .deadline(bug.getDeadLine())
                .createdDate(bug.getCreatedAt())
                .updatedBy(getCurrentUserId())
                .updatedDate(bug.getUpdatedAt())
                .description(bug.getDescription())
                .build();
    }

    // Create new bug
    @Override
    @Transactional
    public BugDTO createBug(BugDTO bugDTO, MultipartFile file) {
        Bug bug = convertToEntity(bugDTO);

        String epicId = bugDTO.getEpicId();
        String storyId = bugDTO.getStoryId();
        String taskId = bugDTO.getTaskId();

        if (epicId != null) {
            Epic epic = epicRepository.findById(epicId)
                    .orElseThrow(() -> new ResourceNotFound("Invalid Epic Id"));
            bug.setEpic(epic);
        } else if (storyId != null) {
            Story story = storyRepository.findById(storyId)
                    .orElseThrow(() -> new ResourceNotFound("Invalid storyId: " + storyId));
            bug.setStory(story);
        } else if (taskId != null) {
            taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFound("Invalid Task ID"));
        }

        bug.setCreatedAt(LocalDate.now());
        bug.setImage(cloudinaryService.uploadImage(file));
        bug.setCreator(getCurrentUserId());
        Bug savedBug = bugRepository.save(bug);

        if (epicId == null && storyId == null && taskId == null) {
            projectFeignClient.addBugToProject(savedBug.getProjectId(), savedBug.getId());
        }

        TaskEvent taskEvent = generateTaskEvent(savedBug);
        taskEvent.setAction(Actions.CREATED);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedBug);
    }

    // Delete bug (soft delete by marking completed)
    @Override
    @Transactional
    public ResponseDTO deleteBug(String bugId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));

        Status oldStatus = bug.getStatus();
        bug.setStatus(Status.COMPLETED);
        bug.setCompletionPercent(100L);
        Bug savedBug = bugRepository.save(bug);

        TaskEvent taskEvent = generateTaskEvent(savedBug);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(Status.COMPLETED);
        taskEvent.setAction(Actions.DELETED);

        producer.sendTaskEvent(taskEvent);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return new ResponseDTO("Bug deleted successfully");
    }

    // Get bug by ID
    @Override
    public BugDTO getBugById(String bugId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));
        return convertToDTO(bug);
    }

    // Update bug
    @Override
    public BugDTO updateBug(BugDTO bugDTO) {
        String id = bugDTO.getId();
        Bug existingBug = bugRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Invalid Bug ID"));

        Status oldStatus = existingBug.getStatus();

        Bug updatedBug = convertToEntity(bugDTO);
        updatedBug.setId(id);
        updatedBug.setCreatedAt(existingBug.getCreatedAt());
        updatedBug.setUpdatedAt(LocalDate.now());
        updatedBug.setAssignees(existingBug.getAssignees());
        updatedBug.setStory(existingBug.getStory());
        updatedBug.setEpic(existingBug.getEpic());

        Bug savedBug = bugRepository.save(updatedBug);

        TaskEvent taskEvent = generateTaskEvent(savedBug);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(savedBug.getStatus());
        taskEvent.setDeadline(savedBug.getDeadLine());
        taskEvent.setAssignees(savedBug.getAssignees());
        taskEvent.setAction(Actions.UPDATED);

        producer.sendTaskEvent(taskEvent);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedBug);
    }

    // Assign bug to user
    @Override
    @Transactional
    public ResponseDTO assignBugToUser(String bugId, String userId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));

        if (bug.getStatus() == Status.COMPLETED) {
            throw new ResourceNotFound("Bug is already marked as completed, can't assign new member");
        }

        bug.getAssignees().add(userId);
        bugRepository.save(bug);

        TaskEvent taskEvent = generateTaskEvent(bug);
        taskEvent.setAssignees(Set.of(userId));
        taskEvent.setAction(Actions.ASSIGNED);

        producer.sendTaskEvent(taskEvent);

        return new ResponseDTO("Bug assigned to user successfully");
    }

    // Change bug status
    @Override
    @Transactional
    public ResponseDTO changeBugStatus(String bugId, Status status) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFound("Bug not found: " + bugId));

        Status oldStatus = bug.getStatus();
        bug.setStatus(status);

        switch (status) {
            case TODO -> bug.setCompletionPercent(0L);
            case IN_PLANNED -> bug.setCompletionPercent(10L);
            case IN_PROGRESS -> bug.setCompletionPercent(60L);
            case IN_QA -> bug.setCompletionPercent(90L);
            case COMPLETED, ARCHIVED -> bug.setCompletionPercent(100L);
        }

         Bug savedBug =  bugRepository.save(bug);

        TaskEvent taskEvent = generateTaskEvent(savedBug);

        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(status);
        taskEvent.setAction(Actions.STATUS_CHANGED);
        taskEvent.setAssignees(bug.getAssignees());

        producer.sendTaskEvent(taskEvent);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return new ResponseDTO("Bug status updated successfully");
    }

    // Get all bugs for a project
    @Override
    public List<BugDTO> getBugsByProjectId(String projectId) {
        return bugRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get bugs assigned to a specific user
    @Override
    public List<BugDTO> getBugsByUserId(String userId) {
        return bugRepository.findByAssignees(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
