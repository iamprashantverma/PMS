package com.pms.TaskService.entity;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("SUBTASK")
public class SubTask extends  Issue{

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;
}
