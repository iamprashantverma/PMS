package com.pms.TaskService.services;

import com.pms.TaskService.dto.IssueDTO;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;

public interface IssueService {

     String setIssueStatus(String issueId, Status status);

    String setIssuePriority(String issueId, Priority priority);

    IssueDTO getIssueById(String id);
}
