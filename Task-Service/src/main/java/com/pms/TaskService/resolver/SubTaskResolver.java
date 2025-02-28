package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.entities.SubTask;
import com.pms.TaskService.services.SubTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@Controller
@Slf4j
@RequiredArgsConstructor
public class SubTaskResolver {

    private final SubTaskService subTaskService;

    @MutationMapping
    public SubTaskDTO createSubTask(@Argument SubTaskDTO subTask) {
        return subTaskService.createSubTask(subTask);
    }

    @MutationMapping
    public ResponseDTO deleteSubTask(@Argument String subTaskId) {
        return subTaskService.deleteSubTask(subTaskId);
    }

    @QueryMapping
    public SubTaskDTO getSubTaskById(@Argument String subTaskId) {
        return subTaskService.getSubTaskById(subTaskId);
    }



}
