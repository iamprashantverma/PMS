package com.pms.TaskService.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a Task, which is a specific type of Issue.
 * A task can be associated with an Epic and can contain multiple SubTasks.
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("TASK") // Value stored in the "issue_type" column for tasks
public class Task extends Issue {

    /**
     * Indicates whether this task is blocking progress on other tasks.
     */
    private boolean isBlocking;

    /**
     * The Epic this task is associated with.
     * Can be null for standalone tasks.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id", referencedColumnName = "id")
    private Epic epic;

    /**
     * The Story this task belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    /**
     * List of subtasks associated with this task.
     */
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubTask> subTasks;
}
