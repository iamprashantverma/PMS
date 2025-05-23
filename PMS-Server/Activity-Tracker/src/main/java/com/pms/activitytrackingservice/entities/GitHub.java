package com.pms.activitytrackingservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "github_changes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitHub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String repositoryName;
    private String branch;
    private String author;
    private String commitMessage;
    private String commitHash;
    private LocalDateTime timestamp;

}
