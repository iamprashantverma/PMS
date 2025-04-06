package com.pms.TaskService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * Represents a comment made on a task.
 * Each comment includes metadata such as creation time, author, and content.
 */
@Entity
@Data
public class Comment {

    /**
     * Unique identifier for the comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    /**
     * Timestamp when the comment was created.
     * Automatically set during persistence.
     */
    @CreationTimestamp
    private LocalDate createdAt;

    /**
     * Identifier of the task this comment is associated with.
     */
    private String taskId;

    /**
     * Identifier of the user who made the comment.
     */
    private String userId;

    /**
     * The content/message of the comment.
     */
    private String message;
}
