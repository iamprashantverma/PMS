package com.pms.TaskService.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SUBTASK")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubTask extends  Issue{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId",referencedColumnName = "id")
    private Task parentTask;
}
