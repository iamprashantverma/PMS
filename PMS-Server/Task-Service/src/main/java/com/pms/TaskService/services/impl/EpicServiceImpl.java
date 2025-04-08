package com.pms.TaskService.services.impl;

import com.pms.TaskService.clients.ProjectFeignClient;
import com.pms.TaskService.clients.UserFeignClient;
import com.pms.TaskService.dto.EpicDTO;
import com.pms.TaskService.dto.ProjectDTO;
import com.pms.TaskService.dto.UserDTO;
import com.pms.TaskService.entities.Epic;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.event.TaskEvent;
import com.pms.TaskService.event.enums.Actions;
import com.pms.TaskService.event.enums.EventType;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.CalendarEventProducer;
import com.pms.TaskService.producer.TaskEventProducer;
import com.pms.TaskService.repository.EpicRepository;
import com.pms.TaskService.services.EpicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpicServiceImpl implements EpicService {

    private final ModelMapper modelMapper;
    private final EpicRepository epicRepository;
    private final TaskEventProducer producer;
    private final ProjectFeignClient projectFeignClient;
    private final UserFeignClient userFeignClient;
    private final CalendarEventProducer calendarEventProducer;

    private TaskEvent getEpicTaskEvent(Epic epic) {
        return TaskEvent.builder()
                .entityId(epic.getId())
                .title(epic.getTitle())
                .projectId(epic.getProjectId())
                .eventType(EventType.EPIC)
                .priority(epic.getPriority())
                .deadline(epic.getDeadLine())
                .description(epic.getDescription())
                .createdDate(epic.getCreatedAt())
                .build();
    }

    private Epic convertToEpicEntity(EpicDTO epicDTO) {
        return modelMapper.map(epicDTO, Epic.class);
    }

    private EpicDTO convertToEpicDTo(Epic epic) {
        return modelMapper.map(epic, EpicDTO.class);
    }

    private Epic getEpicEntity(String epicId) {
        return epicRepository.findById(epicId)
                .orElseThrow(() -> new ResourceNotFound("Epic not found: " + epicId));
    }

    @Override
    @Transactional
    public EpicDTO createEpic(EpicDTO epicDTO) {
        projectFeignClient.getProject(epicDTO.getProjectId());

        Epic epic = convertToEpicEntity(epicDTO);
        epic.setCreatedAt(LocalDate.now());
        Epic savedEpic = epicRepository.save(epic);

        projectFeignClient.addEpicToProject(savedEpic.getProjectId(), savedEpic.getId());

        TaskEvent taskEvent = getEpicTaskEvent(savedEpic);
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAction(Actions.CREATED);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToEpicDTo(savedEpic);
    }

    @Override
    @Transactional
    public EpicDTO updateEpicStatus(String epicId, Status newStatus) {
        Epic existingEpic = getEpicEntity(epicId);
        Status oldStatus = existingEpic.getStatus();

        existingEpic.setStatus(newStatus);
        existingEpic.setUpdatedAt(LocalDate.now());
        Epic savedEpic = epicRepository.save(existingEpic);

        // Trigger event only if status actually changed
        if (!oldStatus.equals(newStatus)) {
            TaskEvent taskEvent = getEpicTaskEvent(savedEpic);
            taskEvent.setOldStatus(oldStatus);
            taskEvent.setNewStatus(newStatus);
            producer.sendTaskEvent(taskEvent);
        }

        TaskEvent taskEvent = getEpicTaskEvent(existingEpic);
        taskEvent.setAction(Actions.STATUS_CHANGED);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setNewStatus(newStatus);
        taskEvent.setUpdatedDate(LocalDate.now());
        taskEvent.setAssignees(savedEpic.getAssignees());

        producer.sendTaskEvent(taskEvent);

        taskEvent.setEventType(EventType.CALENDER);
        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToEpicDTo(savedEpic);
    }

    @Override
    @Transactional
    public EpicDTO deleteEpic(String epicId) {
        Epic epic = getEpicEntity(epicId);

        if (epic.getStatus() == Status.COMPLETED) {
            throw new ResourceNotFound("No Epic found : " + epicId);
        }

        // Check if any story is incomplete
        boolean hasIncompleteStories = epic.getStories().stream()
                .anyMatch(story -> story.getStatus() != Status.COMPLETED);

        // Check if any task is incomplete
        boolean hasIncompleteTasks = epic.getTasks().stream()
                .anyMatch(task -> task.getStatus() != Status.COMPLETED);

        if (hasIncompleteStories || hasIncompleteTasks) {
            throw new IllegalStateException("Epic cannot be deleted as it has incomplete stories or tasks.");
        }

        epic.setStatus(Status.COMPLETED);
        epicRepository.save(epic);

        TaskEvent taskEvent = getEpicTaskEvent(epic);
        taskEvent.setEventType(EventType.CALENDER);
        taskEvent.setAction(Actions.DELETED);
        taskEvent.setNewStatus(Status.COMPLETED);

        calendarEventProducer.sendTaskEvent(taskEvent);

        return convertToEpicDTo(epic);
    }

    @Override
    public EpicDTO getEpicById(String epicId) {
        Epic epic = getEpicEntity(epicId);
        return convertToEpicDTo(epic);
    }

    @Override
    public List<EpicDTO> getAllEpicsByProjectId(String projectId) {
        List<Epic> epics = epicRepository.findAllByProjectId(projectId);
        return epics.stream()
                .filter(epic -> epic.getStatus() != Status.COMPLETED)
                .map(this::convertToEpicDTo)
                .toList();
    }

    @Override
    public EpicDTO assignMemberToEpic(String epicId, String memberId) {
        Epic existingEpic = getEpicEntity(epicId);

        userFeignClient.getUserById(memberId); // Ensure user exists
        existingEpic.getAssignees().add(memberId);

        Epic savedEpic = epicRepository.save(existingEpic);

        TaskEvent taskEvent = getEpicTaskEvent(savedEpic);
        taskEvent.setAction(Actions.ASSIGNED);
        taskEvent.setAssignees(Set.of(memberId));
        taskEvent.setDescription("You are assigned to Epic");

        producer.sendTaskEvent(taskEvent);

        return convertToEpicDTo(savedEpic);
    }

    @Override
    public EpicDTO removeMemberFromEpic(String epicId, String memberId) {
        userFeignClient.getUserById(memberId); // Ensure user exists
        Epic existingEpic = getEpicEntity(epicId);

        existingEpic.getAssignees().remove(memberId);
        Epic savedEpic = epicRepository.save(existingEpic);

        TaskEvent taskEvent = getEpicTaskEvent(savedEpic);
        taskEvent.setAction(Actions.UNASSIGNED);
        taskEvent.setAssignees(Set.of(memberId));
        taskEvent.setDescription("You are unassigned from the Epic " + epicId);

        producer.sendTaskEvent(taskEvent);

        return convertToEpicDTo(savedEpic);
    }

    @Override
    public List<UserDTO> getAssignedMembers(String epicId) {
        Epic epic = getEpicEntity(epicId);
        List<UserDTO> users = new LinkedList<>();

        for (String userId : epic.getAssignees()) {
            users.add(userFeignClient.getUserById(userId));
        }

        return users;
    }
}
