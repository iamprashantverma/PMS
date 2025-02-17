package com.pms.TaskService.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BUG")
public class Bug extends Issue {
    private String stepsToReproduce;
    private String expectedOutcome;
    private String actualOutcome;
}

