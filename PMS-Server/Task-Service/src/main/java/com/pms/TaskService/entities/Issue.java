package com.pms.TaskService.entities;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

/**
 * Abstract base class representing a general issue.
 * Extended by concrete issue types like Bug, Story, etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issues")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EntityListeners(AuditingEntityListener.class)
@DiscriminatorColumn(name = "issue_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Issue {

    /**
     * Unique string ID for the issue, auto-generated before persisting.
     */
    @Id
    private String id;

    @PrePersist
    public void generateId() {
        this.updatedAt = LocalDate.now();
        this.id = String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * Title of the issue.
     */
    private String title;

    /**
     * Detailed description of the issue.
     */
    private String description;

    /**
     * ID of the project this issue belongs to.
     */
    private String projectId;

    /**
     * Set of user IDs assigned to this issue.
     */
    @ElementCollection
    private Set<String> assignees;

    /**
     * ID of the user who created the issue.
     */
    private String creator;

    /**
     * Creation timestamp.
     */
    private LocalDate createdAt;

    /**
     * Last updated timestamp.
     */
    private LocalDate updatedAt;

    @PreUpdate
    public void preUpdate() {
        System.out.println("PreUpdate is Triggered");
        this.updatedAt = LocalDate.now();
    }
    /**
     * Deadline to complete the issue.
     */
    private LocalDate deadLine;

    /**
     * ID of the user who reported the issue.
     */
    private String reporter;

    /**
     * Optional image associated with the issue.
     */
    private String image;

    /**
     * Status of the issue (e.g., OPEN, IN_PROGRESS).
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Priority level of the issue.
     */
    @Enumerated(EnumType.STRING)
    private Priority priority;

    /**
     * Optional label for categorization or filtering.
     */
    private String label;

    /**
     * Completion percentage (0–100) indicating progress.
     */
    private Long completionPercent;
}
