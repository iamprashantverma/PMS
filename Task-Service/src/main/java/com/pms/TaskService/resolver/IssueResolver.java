package com.pms.TaskService.resolver;


import com.pms.TaskService.entities.enums.Priority;
import com.pms.TaskService.entities.enums.Status;
import com.pms.TaskService.services.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IssueResolver {


    private final IssueService issueService;
    @MutationMapping
    public ResponseEntity<String> setIssueStatus(@Argument String issueId, @Argument("status") Status status){
        String response = issueService.setIssueStatus(issueId, status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @MutationMapping
    public ResponseEntity<String> setIssuePriority(@Argument String issueId, @Argument("status") Priority priority){
        String response = issueService.setIssuePriority(issueId, priority);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}


//type Query{
//getTaskById(taskId : ID!): Task
//getAllTaskByProjectId(projectId : ID!) : [Task]
//        }
//
//type Mutation{
//setIssueStatus(issueId : ID!, status: Status) : String
//setIssuePriority(issueId : ID!, priority: Priority) : String
//}
//
//type Issue{
//title: String
//description: String
//createrId: String
//status: Status
//priority: Priority
//tag: IssueTag
//completionPercent: Int
//memberId : [String]
//        }
//
//input IssueDTO{
//title: String
//description: String
//createrId: String
//status: Status
//priority: Priority
//tag: IssueTag
//completionPercent: Int
//}
