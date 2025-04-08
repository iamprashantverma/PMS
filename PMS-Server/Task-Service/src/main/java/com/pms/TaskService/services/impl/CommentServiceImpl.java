package com.pms.TaskService.services.impl;

import com.pms.TaskService.auth.UserContextHolder;
import com.pms.TaskService.dto.CommentDTO;
import com.pms.TaskService.entities.Comment;
import com.pms.TaskService.event.NotificationEvent;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.producer.NotificationEventProducer;
import com.pms.TaskService.repository.CommentRepository;
import com.pms.TaskService.repository.TaskRepository;
import com.pms.TaskService.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final NotificationEventProducer producer;
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    // Get the ID of the currently authenticated user
    private String getCurrentUserId() {
        return UserContextHolder.getCurrentUserId();
    }


    // Sinks for emitting updates
    private final Map<String, Sinks.Many<CommentDTO>> taskCommentSinks = new ConcurrentHashMap<>();

    private CommentDTO convertToDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    private Comment convertToEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    private List<String> extractMentionedUsers(String content) {
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(content);
        return matcher.results().map(m -> m.group(1)).toList();
    }

    @Override
    public CommentDTO addComment(CommentDTO commentDto) {
        taskRepository.findById(commentDto.getTaskId()).orElseThrow(() ->
                new ResourceNotFound("Invalid Task Id"));

        Comment comment = convertToEntity(commentDto);
        Comment savedComment = commentRepository.save(comment);

        // Emit the new comment to subscribers
        sendCommentUpdate(commentDto.getTaskId(), convertToDTO(savedComment));

        // Extract mentioned users
        List<String> mentionedUserIds = extractMentionedUsers(commentDto.getMessage());
        if (!mentionedUserIds.isEmpty()) {
            mentionUsersInComment(savedComment, mentionedUserIds);
        }

        return convertToDTO(savedComment);
    }

    @Override
    public CommentDTO updateComment(String commentId, CommentDTO commentDto) {
        Comment existingComment = commentRepository.findById(Long.parseLong(commentId))
                .orElseThrow(() -> new ResourceNotFound("Comment not found"));

        existingComment.setMessage(commentDto.getMessage());
        Comment updatedComment = commentRepository.save(existingComment);

        // Emit the updated comment to subscribers
        sendCommentUpdate(updatedComment.getTaskId(), convertToDTO(updatedComment));

        return convertToDTO(updatedComment);
    }

    @Override
    public Long deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        return commentId;
    }

    @Override
    public List<CommentDTO> getCommentsByTaskId(String taskId) {
        return commentRepository.findAllByTaskId(taskId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void mentionUsersInComment(Comment comment, List<String> mentionedUserIds) {
        mentionedUserIds.forEach(userId -> {
            NotificationEvent event = NotificationEvent.builder()
                    .message("You are mentioned in a comment on task " + comment.getTaskId())
                    .userId(userId)
                    .id(comment.getTaskId())
                    .build();
            producer.sendNotificationEvent(event);
        });
    }

    @Override
    public Publisher<CommentDTO> subscribeToCommentUpdates(String taskId) {
        // Get or create a sink for the task
        Sinks.Many<CommentDTO> sink = taskCommentSinks.computeIfAbsent(taskId, key ->
                Sinks.many().multicast().directBestEffort());

        // Send existing comments to the new subscriber
        return Flux.concat(
                Flux.fromIterable(getCommentsByTaskId(taskId)), // Send previous comments
                sink.asFlux() // Subscribe to new comments
        );
    }

    private void sendCommentUpdate(String taskId, CommentDTO comment) {
        Sinks.Many<CommentDTO> sink = taskCommentSinks.get(taskId);
        if (sink != null) {
            sink.tryEmitNext(comment);
        }
    }
}
