package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.CommentDTO;
import com.pms.TaskService.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Resolver for handling comment-related operations.
 * Includes queries, mutations, and subscriptions for comments.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentResolver {

    private final CommentService commentService;

    /**
     * Retrieves all comments for a specific task.
     *
     * @param taskId the task ID
     * @return list of comments
     */
    @QueryMapping
    public List<CommentDTO> getCommentsByTaskId(@Argument String taskId) {
        log.info("Fetching comments for task ID: {}", taskId);
        return commentService.getCommentsByTaskId(taskId);
    }

    /**
     * Adds a new comment to a task.
     *
     * @param commentDTO the comment data
     * @return the created comment
     */
    @MutationMapping
    public CommentDTO addComment(@Argument("commentDTO") CommentDTO commentDTO) {
        log.info("Adding comment to task ID: {}", commentDTO.getTaskId());
        return commentService.addComment(commentDTO);
    }

    /**
     * Deletes a comment by ID.
     *
     * @param commentId the comment ID
     * @return ID of the deleted comment
     */
    @MutationMapping
    public Long deleteComment(@Argument Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        return commentService.deleteComment(commentId);
    }

    /**
     * Subscribes to real-time updates for comments on a specific task.
     *
     * @param taskId the task ID
     * @return a stream of CommentDTOs in real-time
     */
    @SubscriptionMapping
    public Publisher<CommentDTO> subscribeToCommentUpdates(@Argument String taskId) {
        log.info("Subscribing to comment updates for task ID: {}", taskId);
        return commentService.subscribeToCommentUpdates(taskId);
    }
}
