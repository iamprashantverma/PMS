package com.pms.TaskService.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@DiscriminatorValue("EPIC") // Discriminator value stored in the "issue_type" column
public class Epic extends Issue {

    /**
     * The main goal or objective of this epic.
     */
    private String epicGoal;

    /**
     * List of stories linked to this epic.
     * CascadeType.ALL ensures stories get deleted when the epic is removed.
     */
    @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Story> stories = new HashSet<>();

    /**
     * List of tasks directly assigned under this epic.
     */
    @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();
}
