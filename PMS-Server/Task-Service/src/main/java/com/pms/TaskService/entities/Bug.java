package com.pms.TaskService.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a bug issue in the system.
 * Inherits from the base Issue entity.
 */
@Entity
@DiscriminatorValue("BUG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bug extends Issue {

    /**
     * Steps required to reproduce the bug.
     */
    private String stepsToReproduce;

    /**
     * Expected behavior after performing the reproduction steps.
     */
    private String expectedOutcome;

    /**
     * Actual behavior observed, which differs from the expected outcome.
     */
    private String actualOutcome;

    /**
     * The Epic to which this bug is related.
     */
    @ManyToOne
    @JoinColumn(name = "epic_id")
    private Epic epic;

    /**
     * The Story under which this bug is categorized.
     */
    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;
}
