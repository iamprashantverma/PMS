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
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.repository.StoryRepository;
import com.pms.TaskService.services.StoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final ModelMapper modelMapper;
    private final StoryRepository storyRepository;
    private final EpicRepository epicRepository;
    private final TaskEventProducer taskEventProducer;

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
                .build();
    }

    @Override
    @Transactional
    public StoryDTO createStory(StoryDTO storyDTO) {
        // convert into the story entity
        Story story = convertToEntity(storyDTO);
        story.setCreatedDate(LocalDateTime.now());
        // saved the new story into the DB
        Story savedStory = storyRepository.save(story);

        // add story into the epic
        String epicId = storyDTO.getEpicId();
        if (epicId ==null)
            throw  new ResourceNotFound("Epic Id has not provided ");
        else
            addStoryOnEpic(epicId, savedStory);

        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setDescription("Story Created");
        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(story);
    }

    @Override
    public StoryDTO updateStory(StoryDTO storyDTO) {

        Story story = getStoryEntity(storyDTO.getId());
        story.setUpdatedDate(LocalDateTime.now());

        Story modifiedStory = modelMapper.map(storyDTO, Story.class);
        // persist into the db
        modifiedStory.setUpdatedDate(LocalDateTime.now());
        Story savedStory =  storyRepository.save(modifiedStory);

        TaskEvent taskEvent = generateTaskEvent(savedStory);
        taskEvent.setAction(Actions.UPDATED);
        taskEvent.setPriority(modifiedStory.getPriority());
        taskEvent.setNewStatus(modifiedStory.getStatus());
        taskEvent.setNewStatus(modifiedStory.getStatus());
        taskEvent.setProjectId(modifiedStory.getProjectId());
        taskEvent.setUpdatedDate(LocalDateTime.now());
        taskEvent.setDescription("Story Updated Successfully ");

        taskEventProducer.sendTaskEvent(taskEvent);

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

        if (!allTasksCompleted) {
            throw new IllegalStateException("Cannot delete story. All associated tasks must be completed first.");
        }
        Status oldStatus = story.getStatus();
        // Mark as ARCHIVED
        story.setStatus(Status.ARCHIVED);
        // Save the updated story
        storyRepository.save(story);

        // Publish Story Deletion Event
        TaskEvent taskEvent = generateTaskEvent(story);
        taskEvent.setAction(Actions.DELETED);
        taskEvent.setNewStatus(Status.ARCHIVED);
        taskEvent.setOldStatus(oldStatus);

        taskEventProducer.sendTaskEvent(taskEvent);

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
        taskEvent.setAssignees(List.of(userId));

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
        taskEvent.setAssignees(List.of(userId));

        taskEventProducer.sendTaskEvent(taskEvent);

        return convertToDTO(savedStory);
    }

    @Override
    @Transactional
    public StoryDTO changeStoryStatus(String storyId, Status status) {
            Story story = getStoryEntity(storyId);
            Status oldStatus = story.getStatus();
            story.setStatus(status);
            storyRepository.save(story);
            TaskEvent taskEvent = generateTaskEvent(story);
            taskEvent.setAction(Actions.UPDATED);
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


