package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.CommentDTO;
import com.pms.TaskService.dto.ResponseDTO;
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
import org.springframework.stereotype.Service;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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

    // Queue for real-time updates
    private final Queue<Comment> commentQueue = new ConcurrentLinkedQueue<>();

    private CommentDTO convertToDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    private Comment convertToEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    private List<String> extractMentionedUsers(String content) {
        List<String> mentionedUsers = new ArrayList<>();
        Pattern pattern = Pattern.compile("@(\\w+)"); // Regex to find mentions (@username)
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            mentionedUsers.add(matcher.group(1)); // Extract username
        }

        return mentionedUsers;
    }

    @Override
    public CommentDTO addComment(CommentDTO commentDto) {
        // Fetch the task (issueId)
        taskRepository.findById(commentDto.getTaskId()).orElseThrow(() ->
                new ResourceNotFound("Invalid Task Id"));
        Comment comment = convertToEntity(commentDto);

        Comment savedComment = commentRepository.save(comment);

        // Emit the comment for real-time subscribers
        commentQueue.offer(savedComment);

        // Extract mentioned users from comment content (e.g., @user123)
        List<String> mentionedUserIds = extractMentionedUsers(commentDto.getMessage());

        // Notify them in their app
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

        commentQueue.offer(updatedComment); // Emit updated comment for real-time updates

        return convertToDTO(updatedComment);
    }

    @Override
    public ResponseDTO deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        return ResponseDTO.builder().message("Comment Deleted").build();
    }

    @Override
    public List<CommentDTO> getCommentsByTaskId(String taskId) {
        List<Comment> comments = commentRepository.findAllByTaskId(taskId);
        return comments.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void mentionUsersInComment(Comment comment, List<String> mentionedUserIds) {
        for (String userId : mentionedUserIds) {
            // Create a notification event
            NotificationEvent event = NotificationEvent.builder()
                    .message("You are mentioned in a comment on task " + comment.getTaskId())
                    .userId(userId)
                    .id(comment.getTaskId())
                    .build();
            // Produce the notification event
            producer.sendNotificationEvent(event);
        }
    }

    @Override
    public Publisher<CommentDTO> subscribeToCommentUpdates(String taskId) {
        return subscriber -> {
            // Send existing comments first
            List<CommentDTO> existingComments = getCommentsByTaskId(taskId);
            existingComments.forEach(subscriber::onNext);

            // Continuously send new comments
            while (!Thread.currentThread().isInterrupted()) {
                Comment comment = commentQueue.poll();
                if (comment != null && comment.getTaskId().equals(taskId)) {
                    subscriber.onNext(convertToDTO(comment));
                }
            }
        };
    }

}
