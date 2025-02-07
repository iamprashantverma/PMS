package com.pms.TaskService.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
@DiscriminatorValue("TASK")
public class Task extends Issue {

    private boolean isBlocking;  // If this task is preventing others from progressing

    @OneToMany
    @JoinColumn(name = "blocked_by_task_id")
    private List<Task> blockedTasks;  // Tasks that this task is blocking

}

