package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.Issue;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.services.EpicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpicServiceImpl implements EpicService {

    private final ModelMapper modelMapper;
    private final EpicRepository epicRepository;
    private final TaskEventProducer producer;

    /**
     * Generates a TaskEvent for an Epic.
     * @param epic The Epic entity.
     * @return TaskEvent
     */
    private TaskEvent getEpicTaskEvent(Epic epic) {
        return TaskEvent.builder()
                .entityId(epic.getId())
                .title(epic.getTitle())
                .projectId(epic.getProjectId())
                .eventType(EventType.TASK)
                .build();
    }

    /**
     * Converts an EpicDTO to an Epic entity.
     * @param epicDTO The DTO containing Epic details.
     * @return The corresponding Epic entity.
     */
    private Epic convertToEpicEntity(EpicDTO epicDTO) {
        return modelMapper.map(epicDTO, Epic.class);
    }

    /**
     * Converts an Epic entity to an EpicDTO.
     * @param epic The Epic entity.
     * @return The corresponding EpicDTO.
     */
    private EpicDTO convertToEpicDTo(Epic epic) {
        return modelMapper.map(epic, EpicDTO.class);
    }

    /**
     * Retrieves an Epic entity by ID.
     * @param epicId The ID of the Epic.
     * @return The Epic entity.
     * @throws ResourceNotFound if not found.
     */
    private Epic getEpicEntity(String epicId) {
        return epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found: " + epicId));
    }

    @Override
    @Transactional
    public EpicDTO createEpic(EpicDTO epicDTO) {
        Epic epic = convertToEpicEntity(epicDTO);
        epic.setStatus(Status.TODO);
        epic.setCreatedDate(LocalDateTime.now());

        Epic savedEpic = epicRepository.save(epic);

        TaskEvent taskEvent = getEpicTaskEvent(savedEpic);
        taskEvent.setAction(Actions.CREATED);
        taskEvent.setNewStatus(epic.getStatus());
        taskEvent.setCreatedDate(savedEpic.getCreatedDate());
        taskEvent.setDescription("Epic Created ");

        producer.sendTaskEvent(taskEvent);

        return convertToEpicDTo(savedEpic);
    }

    @Override
    @Transactional
    public EpicDTO updateEpicStatus(String epicId, Status newStatus) {

        Epic existingEpic = getEpicEntity(epicId);
        Status oldStatus = existingEpic.getStatus();

        existingEpic.setStatus(newStatus);
        Epic savedEpic = epicRepository.save(existingEpic);

        if (!oldStatus.equals(newStatus)) {
            TaskEvent taskEvent = getEpicTaskEvent(savedEpic);
            taskEvent.setOldStatus(oldStatus);
            taskEvent.setNewStatus(newStatus);
            producer.sendTaskEvent(taskEvent);
        }

        return convertToEpicDTo(savedEpic);
    }

    @Override
    @Transactional
    public EpicDTO deleteEpic(String epicId) {
        Epic epic = getEpicEntity(epicId);
        // if epic is already archived then throw error
        if (epic.getStatus() == Status.ARCHIVED)
            throw new ResourceNotFound("No Epic found : "+epicId );

        // Check if any associated stories or tasks are not completed
        boolean hasIncompleteTasks = epic.getStories().stream()
                .anyMatch(story -> story.getStatus() != Status.COMPLETED);

        if (hasIncompleteTasks) {
            throw new IllegalStateException("Epic cannot be deleted as it has incomplete stories or tasks.");
        }
        // set the epic as ARCHIVED
        epic.setStatus(Status.ARCHIVED);
        // Perform deletion
        epicRepository.save(epic);



        return convertToEpicDTo(epic);
    }

    @Override
    public EpicDTO getEpicById(String epicId) {
        Epic epic = getEpicEntity(epicId);
        return convertToEpicDTo(epic);
    }

    @Override
    public List<EpicDTO> getAllActiveEpics() {
        List<Epic> epics = epicRepository.findAll();
        return epics.stream()
                .filter(epic -> epic.getStatus() != Status.ARCHIVED)
                .map(this::convertToEpicDTo)
                .toList();
    }



}
