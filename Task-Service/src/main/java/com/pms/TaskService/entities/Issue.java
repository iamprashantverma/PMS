package com.pms.TaskService.entities;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.List;

/**
 * Base entity representing a generic issue.
 * This entity is extended by other specific issue types like Epic, Story, Task, Bug, etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issues")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Using Single Table Inheritance
@DiscriminatorColumn(name = "issue_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Issue {

    @Id
    private String id;

    /**
     * Generates a unique UUID before persisting the entity.
     * This ensures each issue has a globally unique identifier.
     */
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = String.format("%06d", new Random().nextInt(999999));
        }
    }


    private String title;
    private String description;
    private String projectId;

    /**
     * List of users assigned to the issue.
     * Uses ElementCollection since it's a simple list of strings.
     */
    @ElementCollection
    private Set<String> assignees;
    private String creator;

    private LocalDate createdDate;
    private LocalDate updatedDate;
    private LocalDate deadLine;

    /**
     * Current status of the issue (TODO, IN_PROGRESS, DONE, etc.)
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Priority level of the issue (LOW, MEDIUM, HIGH, CRITICAL)
     */
    @Enumerated(EnumType.STRING)
    private Priority priority;

    /**
     * Completion percentage, helps track progress on issues.
     */
    private Long completionPercent;
}
