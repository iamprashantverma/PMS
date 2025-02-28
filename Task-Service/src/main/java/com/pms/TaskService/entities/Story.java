package com.pms.TaskService.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@DiscriminatorValue("STORY")
public class Story extends Issue {

    /**
     * Defines conditions that must be met for the story to be considered complete.
     */
    private String acceptanceCriteria;

    /**
     * Many stories can belong to one epic.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id", referencedColumnName = "id")
    private Epic epic;

    /**
     * A Story can have multiple tasks.
     */
    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Task> tasks;
}
