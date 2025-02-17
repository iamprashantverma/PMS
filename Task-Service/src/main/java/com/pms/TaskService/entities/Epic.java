package com.pms.TaskService.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EPIC")
public class Epic extends Issue {


    private String epicGoal;

}

