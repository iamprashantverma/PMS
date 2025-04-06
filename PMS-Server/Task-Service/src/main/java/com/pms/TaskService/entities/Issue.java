package com.pms.TaskService.entities;

import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issues")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "issue_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Issue {

    @Id
    private String id;

    @PrePersist
    public void generateId() {
        this.id = String.format("%06d", new Random().nextInt(999999));
    }

    private String title;
    private String description;
    private String projectId;

    @ElementCollection
    private Set<String> assignees;
    private String creator;

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private LocalDate deadLine;
    private String reporter;
    private String image;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;
    private  String label;
    private Long completionPercent;
}
