package com.pms.TaskService.services.impl;

import com.pms.TaskService.dto.IssueDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.Issue;
import com.pms.TaskService.entities.enums.IssueTag;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.IssueStatus;
import com.pms.TaskService.exceptions.ResourceNotFound;
import com.pms.TaskService.repository.IssueRepository;
import com.pms.TaskService.services.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j

public class IssueServiceImpl implements IssueService {

    private final ModelMapper modelMapper;
    private final IssueRepository issueRepository;



    @Override
    public ResponseDTO updateIssueStatus(String issueId, IssueStatus status) {
        Issue issue = issueRepository.findById(issueId)
                        .orElseThrow(()-> new ResourceNotFound("Issue not found with id "+issueId));
        issue.setStatus(status);
        issueRepository.save(issue);
        log.info(String.valueOf(status));
        return new ResponseDTO("update status with ");
    }

    @Override
    public ResponseDTO updateIssuePriority(String issueId, Priority priority) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(()-> new ResourceNotFound("Issue not found with id "+issueId));
        issue.setPriority(priority);
        issueRepository.save(issue);
        log.info(String.valueOf(priority));
        return new ResponseDTO("update status with ");
    }

    @Override
    public IssueDTO getIssueById(String id) {
        Issue issue =  issueRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Issue not found wth id "+id));

        return modelMapper.map(issue, IssueDTO.class);
    }

    @Override
    public ResponseDTO updateIssueTag(String issueId, IssueTag tag) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(()-> new ResourceNotFound("Issue not found with id "+issueId));
        issue.setTag(tag);
        issueRepository.save(issue);
        log.info(String.valueOf(tag));
        return new ResponseDTO("update status with ");
    }
}
