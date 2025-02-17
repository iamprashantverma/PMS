package com.pms.TaskService.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("TASK")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task extends Issue {

    // If this task is preventing others from progressing
    private boolean isBlocking;
    private List<String> memberId;
    // Tasks that this task is blocking
//    @OneToMany
//    @JoinColumn(name = "blocked_by_task_id")
//    private List<Task> blockedTasks;


}

