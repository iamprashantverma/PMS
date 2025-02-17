package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.IssueDTO;
import com.pms.TaskService.entities.Issue;
import com.pms.TaskService.entities.Task;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.repository.IssueRepository;
import com.pms.TaskService.services.IssueService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class IssueSerivceImpl implements IssueService {

    private final ModelMapper modelMapper;
    private final IssueRepository issueRepository;
//    @Override
//    public String setTaskPriority(String taskId, Priority priority) {
//        Task task = modelMapper.map(getTaskById(taskId), Task.class);
//        task.setPriority(priority);
//        taskRepository.save(task);
//        return "Status update successfully with priority " + task.getPriority();
//    }


    @Override
    public String setIssueStatus(String issueId, Status status) {
        Issue issue = modelMapper.map(getIssueById(issueId), Issue.class);
        issue.setStatus(status);
        issueRepository.save(issue);
        return "update status with "+ issue.getStatus();
    }

    @Override
    public String setIssuePriority(String issueId, Priority priority) {
        return "";
    }

    @Override
    public IssueDTO getIssueById(String id) {
        Issue issue =  issueRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Issue not found wth id "+id));
        return modelMapper.map(issue, IssueDTO.class);
    }
}
