package com.pms.TaskService.services;

import com.pms.TaskService.dto.IssueDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.enums.IssueTag;
import com.pms.TaskService.entities.enums.Priority;

import com.pms.TaskService.entities.enums.Status;

public interface IssueService {

    public ResponseDTO updateIssueStatus(String issueId, Status status);

    public ResponseDTO updateIssuePriority(String issueId, Priority priority);

    public IssueDTO getIssueById(String id);

    public ResponseDTO updateIssueTag(String issueId, IssueTag tag);
}
