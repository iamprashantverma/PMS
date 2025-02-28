package com.pms.TaskService.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


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
    private List<Story> stories = new ArrayList<>();

    /**
     * List of tasks directly assigned under this epic.
     */
    @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;
}
