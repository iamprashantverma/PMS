package com.pms.TaskService.entities;


import com.pms.TaskService.entities.enums.IssueTag;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="issues")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "issue_type", discriminatorType = DiscriminatorType.STRING)

public class Issue {

    @Id
    private String id;

    @PrePersist
    public void generateId() {
        // Avoid overwriting an already set ID
        if (this.id == null) {
            // Generates a unique ID
            this.id = UUID.randomUUID().toString();
        }
    }

    private String title;
    private String description;
    private String project;

    private List<String> assignees;
    private String creater;

    @CreationTimestamp
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Enumerated(EnumType.STRING)
    private Status status;  // (e.g., TODO, IN_PROGRESS, DONE)

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private Long completionPercent;

    @Enumerated(EnumType.STRING)
    private IssueTag tag;

//    private List<String> attachment;
//
//    private List<String> comments;



}
