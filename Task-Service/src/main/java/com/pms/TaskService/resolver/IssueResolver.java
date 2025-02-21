package com.pms.TaskService.resolver;


import com.pms.TaskService.dto.IssueDTO;
import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.entities.enums.IssueTag;
import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.IssueStatus;
import com.pms.TaskService.services.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@RequiredArgsConstructor
@Slf4j
@Controller
public class IssueResolver {


    private final IssueService issueService;

    /* get issue by id */
    @QueryMapping("getIssueById")
    public IssueDTO getIssueById(@Argument String issueId){
        return issueService.getIssueById(issueId);
    }

    /* update Issue status */
    @MutationMapping("updateIssueStatus")
    public ResponseDTO updateIssueStatus(@Argument String issueId, @Argument("status") IssueStatus status){
        return issueService.updateIssueStatus(issueId, status);
    }

    /* update Issue priority */
    @MutationMapping("updateIssuePriority")
    public ResponseDTO updateIssuePriority(@Argument String issueId, @Argument("status") Priority priority){
        return issueService.updateIssuePriority(issueId, priority);
    }

    /* update Issue Tag */
    @MutationMapping("updateIssueTag")
    public ResponseDTO updateIssueTag(@Argument String issueId, @Argument("tag") IssueTag tag){
        return issueService.updateIssueTag(issueId, tag);
    }





}


