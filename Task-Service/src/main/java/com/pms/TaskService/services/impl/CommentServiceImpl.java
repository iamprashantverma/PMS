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
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
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

    //  Add commentSink for real-time updates
    private final Sinks.Many<Comment> commentSink = Sinks.many().multicast().onBackpressureBuffer();

    private CommentDTO convertToDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    private Comment convertToEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO,Comment.class);
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
        // fetch the task( issueId)
         taskRepository.findById(commentDto.getTaskId()).orElseThrow(()->
                 new ResourceNotFound("Invalid Task Id"));
        Comment comment = convertToEntity(commentDto);

        Comment savedComment = commentRepository.save(comment);

        // Emit the comment for real-time subscribers
        commentSink.tryEmitNext(savedComment);

        // Extract mentioned users from comment content (e.g., @user123)
        List<String> mentionedUserIds = extractMentionedUsers(commentDto.getMessage());

        // Notify them in their app
        if (!mentionedUserIds.isEmpty()) {
            mentionUsersInComment(savedComment, mentionedUserIds);
        }

       CommentDTO commentDTO  =  convertToDTO(savedComment);
        System.out.println(commentDTO);
        return commentDTO;
    }

    @Override
    public CommentDTO updateComment(String commentId, CommentDTO commentDto) {
        return null;
    }

    @Override
    public ResponseDTO deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        return ResponseDTO.builder().message("comment Deleted").build();
    }

    @Override
    public List<CommentDTO> getCommentsByTaskId(String postId) {
        List<Comment> comments = commentRepository.findAllByTaskId(postId);
        return comments.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void mentionUsersInComment(Comment comment, List<String> mentionedUserIds) {
        for (String userId : mentionedUserIds) {
            // Create a notification event
            NotificationEvent event = NotificationEvent.builder()
                    .message("You are mentioned in a comment " + comment.getTaskId())
                    .userId(userId)
                    .id(comment.getTaskId())
                    .build();
            // Produce the notification event
            producer.sendNotificationEvent(event);
        }
    }

    @Override
    public Flux<CommentDTO> subscribeToCommentUpdates(String postId) {
        // Fetch existing comments from the database
        Flux<CommentDTO> existingComments = Flux.fromIterable(commentRepository.findAllByTaskId(postId))
                .map(this::convertToDTO);

        // Stream new comments using commentSink
        Flux<CommentDTO> newCommentStream = commentSink.asFlux()
                .filter(comment -> comment.getTaskId().equals(postId)) // Filter for the correct task
                .map(this::convertToDTO);

        // Combine both: First return existing comments, then subscribe to new ones
        return Flux.concat(existingComments, newCommentStream);
    }


}
