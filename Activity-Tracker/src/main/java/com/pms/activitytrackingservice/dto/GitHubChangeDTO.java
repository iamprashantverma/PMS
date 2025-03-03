package com.pms.activitytrackingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitHubChangeDTO {
    private String eventType; // push, pull_request, issue_comment
    private String repositoryName;
    private String branch;
    private String author;
    private String commitMessage;
    private String commitHash;
    private LocalDateTime timestamp;
}
