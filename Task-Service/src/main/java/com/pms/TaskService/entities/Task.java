package com.pms.TaskService.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.action.internal.OrphanRemovalAction;

import java.util.List;

@Entity
@Getter
@Setter
@DiscriminatorValue("TASK") // "issue_type" column will store "TASK"
public class Task extends Issue {

    /**
     * If true, this task is preventing others from progressing.
     */
    private boolean isBlocking;

    /**
     * Many tasks can be linked to an epic.
     * If epic is null, this is a standalone task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id", referencedColumnName = "id")
    private Epic epic;

    /**
     * List of subtasks associated with this task.
     */
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubTask> subTasks;


}

