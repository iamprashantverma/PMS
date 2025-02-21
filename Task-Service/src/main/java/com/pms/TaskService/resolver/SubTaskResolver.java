package com.pms.TaskService.resolver;

import com.pms.TaskService.dto.ResponseDTO;
import com.pms.TaskService.dto.SubTaskDTO;
import com.pms.TaskService.dto.SubTaskInputDTO;
import com.pms.TaskService.entities.SubTask;
import com.pms.TaskService.services.SubTaskService;
import jakarta.transaction.Transactional;
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

    @MutationMapping("createSubTask")
    public SubTaskDTO createSubTask(@Argument SubTaskInputDTO subTask) {
        return subTaskService.createSubTask(subTask);
    }

    @MutationMapping("deleteSubTask")
    public ResponseDTO deleteSubTask(@Argument String subTaskId) {
        return subTaskService.deleteSubTask(subTaskId);
    }

    @QueryMapping("getSubTaskById")
    public SubTask getSubTaskById(@Argument String subTaskId) {
        return subTaskService.getSubTaskById(subTaskId);
    }

    @MutationMapping("addSubTaskOnTask")
    public ResponseDTO addSubTaskOnTask(@Argument String taskId, @Argument String subTaskId) {
        return subTaskService.addSubTaskOnTask(taskId, subTaskId);
    }
}
