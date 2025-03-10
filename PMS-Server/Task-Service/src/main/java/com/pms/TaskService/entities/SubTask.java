package com.pms.TaskService.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("SUBTASK") // "issue_type" column will store "SUBTASK"
public class SubTask extends Issue {

    /**
     * Many subtasks can belong to a single parent task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task parentTask;

}

