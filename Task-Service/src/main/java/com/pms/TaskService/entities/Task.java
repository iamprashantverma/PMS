package com.pms.TaskService.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
@DiscriminatorValue("TASK")
public class Task extends Issue {

    // If this task is preventing others from progressing
    private boolean isBlocking;

    // Tasks that this task is blocking
    @OneToMany
    @JoinColumn(name = "blocked_by_task_id")
    private List<Task> blockedTasks;


}

