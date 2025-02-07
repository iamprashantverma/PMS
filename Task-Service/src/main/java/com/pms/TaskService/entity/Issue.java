package com.pms.TaskService.entity;


import com.pms.TaskService.entity.enums.IssueTag;
import com.pms.TaskService.entity.enums.Priority;
import com.pms.TaskService.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="issues")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "issue_type", discriminatorType = DiscriminatorType.STRING)
public class Issue {
    @Id
    private String id;

    private String title;
    private String description;

    private List<String> assigneeId;  //members
    private String reporterId;  //creater
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Enumerated(EnumType.STRING)
    private Status status;  // (e.g., TODO, IN_PROGRESS, DONE)

    @Enumerated(EnumType.STRING)
    private Priority priority;  //LOW HIGH

    private Long completionPercent;

    @Enumerated(EnumType.STRING)
    private IssueTag tag;  // frontend or backend

//    private List<String> attachment;
//
//    private List<String> comments;



}
