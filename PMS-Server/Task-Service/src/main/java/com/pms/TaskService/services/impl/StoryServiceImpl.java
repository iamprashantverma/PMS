package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.StoryDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.Story;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.CalendarEventProducer;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repositories.EpicRepository;
import com.pms.TaskService.repositories.StoryRepository;
import com.pms.TaskService.services.CloudinaryService;
import com.pms.TaskService.services.StoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.pms.TaskService.auth.UserContextHolder.getCurrentUserId;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final ModelMapper modelMapper;
    private final StoryRepository storyRepository;
    private final EpicRepository epicRepository;
    private final TaskEventProducer taskEventProducer;
    private final CalendarEventProducer calendarEventProducer;
    private final CloudinaryService cloudinaryService;

    // Utility method to convert DTO to entity
    private Story convertToEntity(StoryDTO storyDTO) {
        return modelMapper.map(storyDTO, Story.class);
    }

    // Utility method to convert entity to DTO
    private StoryDTO convertToDTO(Story story) {
        return modelMapper.map(story, StoryDTO.class);
    }

    // Utility method to fetch Story entity by ID or throw exception
    private Story getStoryEntity(String storyId) {
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFound("Story not found: " + storyId));
    }

    // Generates a TaskEvent from Story object
    private TaskEvent generateTaskEvent(Story story) {
        return TaskEvent.builder()
                .entityId(story.getId())
                .title(story.getTitle())
                .projectId(story.getProjectId())
                .eventType(EventType.STORY)
                .assignees(story.getAssignees())
                .deadline(story.getDeadLine())
                .createdDate(story.getCreatedAt())
                .description(story.getDescription())
                .priority(story.getPriority())
                .build();
    }

    // Create a new story with optional image upload and add it to an epic
    @Override
    @Transactional
    public StoryDTO createStory(StoryDTO storyDTO, MultipartFile file) {
        Story story = convertToEntity(storyDTO);
        String imageUrl = cloudinaryService.uploadImage(file);
        story.setImage(imageUrl);
        story.setCreator(getCurrentUserId());
        Story savedStory = storyRepository.save(story);

        if (storyDTO.getEpicId() == null) {
            throw new ResourceNotFound("Epic Id has not been provided");
        } else {
            addStoryOnEpic(storyDTO.getEpicId(), savedStory);
        }

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setNewStatus(savedStory.getStatus());

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(story);
    }

    // Update an existing story
    @Override
    public StoryDTO updateStory(StoryDTO storyDTO) {
        Story story = getStoryEntity(storyDTO.getId());

        if (story.getStatus() == Status.COMPLETED) {
            throw new ResourceNotFound("Story is already completed. Cannot modify.");
        }

        story.setUpdatedAt(LocalDate.now());

        Story modifiedStory = modelMapper.map(storyDTO, Story.class);
        modifiedStory.setUpdatedAt(LocalDate.now());
        modifiedStory.setAssignees(story.getAssignees());
        modifiedStory.setTasks(story.getTasks());
        modifiedStory.setEpic(story.getEpic());
        modifiedStory.getBugs().addAll(story.getBugs());
        modifiedStory.setProjectId(story.getProjectId());

        Story savedStory = storyRepository.save(modifiedStory);

        TaskEvent taskEvent = generateTaskEvent(savedStory);
        taskEvent.setAction(Actions.UPDATED);
        taskEvent.setPriority(modifiedStory.getPriority());
        taskEvent.setNewStatus(modifiedStory.getStatus());
        taskEvent.setProjectId(modifiedStory.getProjectId());
        taskEvent.setUpdatedDate(LocalDate.now());
        taskEvent.setDescription("Story updated successfully");

        taskEventProducer.sendTaskEvent(taskEvent);
        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedStory);
    }

    // Delete or archive story only if all its tasks and bugs are completed
    @Override
    @Transactional
    public StoryDTO deleteStory(String storyId) {
        Story story = getStoryEntity(storyId);

        boolean allTasksCompleted = story.getTasks().stream()
                .allMatch(task -> task.getStatus() == Status.COMPLETED);

        boolean allBugsCompleted = story.getBugs().stream()
                .allMatch(bug -> bug.getStatus() == Status.COMPLETED);

        if (!allTasksCompleted || !allBugsCompleted) {
            throw new IllegalStateException("Cannot delete story. All associated tasks and bugs must be completed.");
        }

        Status oldStatus = story.getStatus();
        story.setStatus(Status.COMPLETED);

        storyRepository.save(story);

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.DELETED);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(Status.COMPLETED);

        taskEventProducer.sendTaskEvent(taskEvent);
        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(story);
    }

    @Override
    public StoryDTO getStoryById(String storyId) {
        return convertToDTO(getStoryEntity(storyId));
    }

    @Override
    public List<StoryDTO> getAllStoriesByProjectId(String projectId) {
        return storyRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<StoryDTO> getAllStoriesByUserId(String userId) {
        // Placeholder for implementation
        return List.of();
    }

    // Assign a user to a story
    @Override
    @Transactional
    public StoryDTO assignUserToStory(String storyId, String userId) {
        Story story = getStoryEntity(storyId);
        story.getAssignees().add(userId);

        Story savedStory = storyRepository.save(story);

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.ASSIGNED);
        taskEvent.setAssignees(Set.of(userId));

        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedStory);
    }

    // Unassign a user from a story
    @Override
    @Transactional
    public StoryDTO unassignUserFromStory(String storyId, String userId) {
        Story story = getStoryEntity(storyId);
        story.getAssignees().remove(userId);

        Story savedStory = storyRepository.save(story);

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.UNASSIGNED);
        taskEvent.setAssignees(Set.of(userId));

        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedStory);
    }

    // Change status of a story
    @Override
    @Transactional
    public StoryDTO changeStoryStatus(String storyId, Status status) {
        Story story = getStoryEntity(storyId);

        Status oldStatus = story.getStatus();
        story.setStatus(status);
        story.setUpdatedAt(LocalDate.now());

        storyRepository.save(story);

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.STATUS_CHANGED);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(status);
        taskEvent.setUpdatedDate(LocalDate.now());

        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(story);
    }

    @Override
    public List<StoryDTO> getStoriesByStatus(Status status) {
        return storyRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Adds a story to the corresponding epic
    @Override
    @Transactional
    public void addStoryOnEpic(String epicId, Story story) {
        Epic epic = epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found: " + epicId));

        if (epic.getStatus() == Status.COMPLETED) {
            throw new ResourceNotFound("Epic is already marked as completed or deleted");
        }

        epic.getStories().add(story);
        story.setEpic(epic);

        epicRepository.save(epic);
        storyRepository.save(story);

        // Optional: return a meaningful response if needed
        ResponseDTO.builder()
                .message("Story added to the epic")
                .build();
    }
}
