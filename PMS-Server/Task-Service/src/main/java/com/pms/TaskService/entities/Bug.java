package com.pms.TaskService.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("BUG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bug extends Issue {

    /**
     * Steps to reproduce the bug.
     */
    private String stepsToReproduce;

    /**
     * Expected behavior after executing the steps.
     */
    private String expectedOutcome;

    /**
     * Actual behavior that differs from the expected outcome.
     */
    private String actualOutcome;

    @ManyToOne
    @JoinColumn(name = "epic_id")
    private Epic epic;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;


}
