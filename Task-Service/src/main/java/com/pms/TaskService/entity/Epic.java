package com.pms.TaskService.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EPIC")
public class Epic extends Issue {

    private String epicGoal;

}

