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
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "epicId",referencedColumnName = "id")
    @JsonIgnore
    private Epic epic;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SubTask> subTasks;


}

