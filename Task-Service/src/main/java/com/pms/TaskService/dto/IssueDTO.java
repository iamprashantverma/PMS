package com.pms.TaskService.dto;

import com.pms.TaskService.entities.enums.IssueTag;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;

import java.util.List;

public class IssueDTO {
    private String title;
    private String description;
    private String createrId;  //creater
    private Status status;
    private Priority priority;
    private IssueTag tag; // frontend or backend
    private Long completionPercent;
    private List<String> memberId;
}
