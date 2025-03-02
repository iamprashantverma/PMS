package com.pms.TaskService.services;

import com.pms.TaskService.dto.CommentDTO;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.Comment;
import reactor.core.publisher.Flux;
import java.util.List;

/**
 * Service interface for managing comments in the Task Service.
 * Provides methods for adding, updating, deleting, and retrieving comments,
 * as well as handling user mentions and real-time updates.
 */
public interface CommentService {

    /**
     * Adds a new comment to a post/task.
     *
     * @param commentDto The DTO containing comment details.
     * @return The created Comment object.
     */
    CommentDTO addComment(CommentDTO commentDto);

    /**
     * Updates an existing comment.
     *
     * @param commentId  The unique ID of the comment to update.
     * @param commentDto The DTO containing updated comment details.
     * @return The updated Comment object.
     */
    CommentDTO updateComment(String commentId, CommentDTO commentDto);

    /**
     * Deletes a comment by its ID.
     *
     * @param commentId The unique ID of the comment to delete.
     */
    ResponseDTO deleteComment(Long commentId);

    /**
     * Retrieves all comments for a given post/task.
     *
     * @param taskId  unique ID of the post/task.
     * @return A list of comments associated with the specified post.
     */
    List<CommentDTO> getCommentsByTaskId(String taskId);

    /**
     * Extracts mentioned users from a comment and triggers notifications.
     *
     * @param commentId         The ID of the comment where users are mentioned.
     * @param mentionedUserIds  A list of user IDs who were mentioned.
     */
    void mentionUsersInComment(Comment commentId, List<String> mentionedUserIds);

    /**
     * Subscribes to real-time comment updates for a specific post/task.
     * This method is used for GraphQL Subscriptions to provide live updates.
     *
     * @param postId The unique ID of the post/task.
     * @return A Flux stream of Comment objects for real-time updates.
     */
    Flux<CommentDTO> subscribeToCommentUpdates(String postId);
}
