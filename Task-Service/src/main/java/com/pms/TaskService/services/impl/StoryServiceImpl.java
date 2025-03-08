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
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.repository.StoryRepository;
import com.pms.TaskService.services.StoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final ModelMapper modelMapper;
    private final StoryRepository storyRepository;
    private final EpicRepository epicRepository;
    private final TaskEventProducer taskEventProducer;
    private final CalendarEventProducer calendarEventProducer;

    /**
     * Converts a StoryDTO to a Story entity.
     *
     * @param storyDTO The StoryDTO object.
     * @return The corresponding Story entity.
     */
    private Story convertToEntity(StoryDTO storyDTO) {
        return modelMapper.map(storyDTO, Story.class);
    }

    /**
     * Converts a Story entity to a StoryDTO.
     *
     * @param storyId The Story entity.
     * @return The corresponding StoryDTO object.
     */
    private StoryDTO convertToDTO(Story storyId) {
        return modelMapper.map(storyId, StoryDTO.class);
    }

    /**
     * find the Story Entity by storyId
     * @param storyId story id to find
     * @return Story
     */
    private Story getStoryEntity(String  storyId) {
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFound("Story not found: " + storyId));
    }

    /**
     * Generates a TaskEvent for Kafka messaging.
     *
     * @param story  The Story entity.
     * @return The generated TaskEvent.
     */

    private TaskEvent generateTaskEvent(Story story) {
        return TaskEvent.builder()
                .entityId(story.getId())
                .title(story.getTitle())
                .projectId(story.getProjectId())
                .eventType(EventType.STORY)
                .assignees(story.getAssignees())
                .deadline(story.getDeadLine())
                .createdDate(story.getCreatedDate())
                .description(story.getDescription())
                .priority(story.getPriority())
                .build();
    }

    @Override
    @Transactional
    public StoryDTO createStory(StoryDTO storyDTO) {
        // convert into the story entity
        Story story = convertToEntity(storyDTO);
        story.setCreatedDate(LocalDate.now());
        // saved the new story into the DB
        Story savedStory = storyRepository.save(story);

        // add story into the epic
        String epicId = storyDTO.getEpicId();
        if (epicId ==null)
            throw  new ResourceNotFound("Epic Id has not provided ");
        else
            addStoryOnEpic(epicId, savedStory);
        TaskEvent taskEvent  = generateTaskEvent(story);
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setNewStatus(savedStory.getStatus());

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(story);
    }

    @Override
    public StoryDTO updateStory(StoryDTO storyDTO) {

        Story story = getStoryEntity(storyDTO.getId());
        if (story.getStatus() == Status.COMPLETED)
            throw  new ResourceNotFound("Story is Already Completed , Can't Modified");
        story.setUpdatedDate(LocalDate.now());

        Story modifiedStory = modelMapper.map(storyDTO, Story.class);
        // persist into the db
        modifiedStory.setUpdatedDate(LocalDate.now());
        modifiedStory.setAssignees(story.getAssignees());
        modifiedStory.setTasks(story.getTasks());
        modifiedStory.setEpic(story.getEpic());
        modifiedStory.getBugs().addAll(story.getBugs());
        modifiedStory.setProjectId(story.getProjectId());

        Story savedStory =  storyRepository.save(modifiedStory);

        TaskEvent taskEvent = generateTaskEvent(savedStory);

        taskEvent.setAction(Actions.UPDATED);
        taskEvent.setPriority(modifiedStory.getPriority());
        taskEvent.setNewStatus(modifiedStory.getStatus());
        taskEvent.setNewStatus(modifiedStory.getStatus());
        taskEvent.setProjectId(modifiedStory.getProjectId());
        taskEvent.setUpdatedDate(LocalDate.now());
        taskEvent.setDescription("Story Updated Successfully ");

        taskEventProducer.sendTaskEvent(taskEvent);

        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedStory);
    }

    @Override
    @Transactional
    public StoryDTO deleteStory(String storyId) {

        // Fetch the story from the database
        Story story = getStoryEntity(storyId);

        // Check if all tasks related to this story are completed
        boolean allTasksCompleted = story.getTasks().stream()
                .allMatch(task -> task.getStatus() == Status.COMPLETED);
        boolean allBugsCompleted = story.getBugs().stream()
                .allMatch(bug -> bug.getStatus() == Status.COMPLETED);

        if (!allTasksCompleted || !allBugsCompleted) {
            throw new IllegalStateException("Cannot delete story. All associated tasks must be completed first.");
        }

        Status oldStatus = story.getStatus();
        // Mark as ARCHIVED
        story.setStatus(Status.COMPLETED);
        // Save the updated story
        storyRepository.save(story);

        // Publish Story Deletion Event
        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.DELETED);
        taskEvent.setNewStatus(Status.COMPLETED);
        taskEvent.setOldStatus(oldStatus);

        taskEventProducer.sendTaskEvent(taskEvent);

        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(story);
    }

    @Override
    public StoryDTO getStoryById(String storyId) {
        Story story = getStoryEntity(storyId);
        return convertToDTO(story);
    }

    @Override
    public List<StoryDTO> getAllStoriesByProjectId(String projectId) {
        List<Story> stories  = storyRepository.findByProjectId(projectId);
        return stories.stream()
                .map(this::convertToDTO)
                .toList();

    }

    @Override
    public List<StoryDTO> getAllStoriesByUserId(String userId) {
        return List.of();
    }
    @Override
    @Transactional
    public StoryDTO assignUserToStory(String storyId, String userId) {
        Story story = getStoryEntity(storyId);
        story.getAssignees().add(userId);
        // persist the new changes into the db
        Story savedStory =  storyRepository.save(story);

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.ASSIGNED);
        taskEvent.setAssignees(Set.of(userId));

        taskEventProducer.sendTaskEvent(taskEvent);
        return convertToDTO(savedStory);

    }

    @Override
    @Transactional
    public StoryDTO unassignUserFromStory(String storyId, String userId) {

        Story story = getStoryEntity(storyId) ;
        story.getAssignees().remove(userId);
        Story savedStory =  storyRepository.save(story);

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.UNASSIGNED);
        taskEvent.setAssignees(Set.of(userId));

        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedStory);
    }

    @Override
    @Transactional
    public StoryDTO changeStoryStatus(String storyId, Status status) {

            Story story = getStoryEntity(storyId);
            story.setUpdatedDate(LocalDate.now());
            Status oldStatus = story.getStatus();
            story.setStatus(status);
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
        List<Story> stories = storyRepository.findByStatus(status);
        return stories.stream().
                map(this::convertToDTO).
                toList();
    }

    @Override
    @Transactional
    public void addStoryOnEpic(String epicId, Story story) {
        Epic epic = epicRepository.findById(epicId).orElseThrow(()->
                new ResourceNotFound("Epic not found : "+epicId));
        if (epic.getStatus() == Status.COMPLETED)
                throw  new ResourceNotFound("Epic is already marked as completed or deleted");
        epic.getStories().add(story);
        story.setEpic(epic);

        // persist the changed into the database
        epicRepository.save(epic) ;
        storyRepository.save(story);

        ResponseDTO.builder()
                .message("story added to the epics")
                .build();

    }

}


