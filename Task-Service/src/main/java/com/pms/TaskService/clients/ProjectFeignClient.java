package com.pms.TaskService.clients;

import com.pms.TaskService.dto.ProjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "project-service",path = "/project")
public interface ProjectFeignClient {

    @GetMapping
    ProjectDTO getProject(@RequestParam("projectId") String projectId);
}
