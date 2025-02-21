package com.pms.TaskService.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("STORY")
@Getter
@Setter
public class Story extends Issue {

    /*the conditions that must be met for the story to be considered complete.*/
    private String acceptanceCriteria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epicId",referencedColumnName = "id")
    @JsonIgnore
    private Epic epic;

}
