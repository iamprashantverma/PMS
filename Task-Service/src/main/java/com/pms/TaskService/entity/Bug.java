package com.pms.TaskService.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BUG")
public class Bug extends Issue {


    private String stepsToReproduce; // it is a description how bug came
    private String expectedOutcome;
    private String actualOutcome;

}

