package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.CommentDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Resolver for handling comments-related queries, mutations, and subscriptions.
 * Provides functionality to fetch, add, delete, and subscribe to comment updates.
 */
@Controller
@Component
@Slf4j
@RequiredArgsConstructor
public class CommentResolver {

    private final CommentService commentService;

    /**
     * Fetches all comments associated with a given task.
     *
     * @param taskId The ID of the task.
     * @return A list of comments related to the specified task.
     */
    @QueryMapping
    public List<CommentDTO> getCommentsByTaskId(String taskId) {
        log.info("Fetching comments for task ID: {}", taskId);
        return commentService.getCommentsByTaskId(taskId);
    }

    /**
     * Adds a new comment to a task.
     *
     * @param commentDto The comment details to be added.
     * @return The created comment.
     */
    @MutationMapping
    public CommentDTO addComment(@Argument("commentDTO") CommentDTO commentDto) {
        log.info("Adding new comment to task ID: {}", commentDto.getTaskId());
        return commentService.addComment(commentDto);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param commentId The ID of the comment to be deleted.
     * @return A response indicating success or failure.
     */
    @MutationMapping
    public ResponseDTO deleteComment(Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        return commentService.deleteComment(commentId);

    }

    /**
     * Subscribes to real-time comment updates for a specific post.
     * Users will receive updates whenever a new comment is added or updated.
     *
     * @param taskId The ID of the post/task for which updates are required.
     * @return A stream of comment updates in real-time.
     */
    @SubscriptionMapping
    public Publisher<CommentDTO> subscribeToCommentUpdates(@Argument("taskId") String taskId) {
        log.info("Subscribing to comments for post ID: {}", taskId);
        return commentService.subscribeToCommentUpdates(taskId);
    }

}
