package com.pms.TaskService.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STORY")
public class Story extends Issue {

    /*the conditions that must be met for the story to be considered complete.*/
    private String acceptanceCriteria;

}
