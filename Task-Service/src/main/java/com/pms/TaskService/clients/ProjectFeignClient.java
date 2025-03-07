package com.pms.TaskService.clients;

import com.pms.TaskService.dto.ProjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "project-service", path = "/project") // Matches the controller base path
public interface ProjectFeignClient {

    @GetMapping
    ProjectDTO getProject(@RequestParam("projectId") String projectId);

    @PostMapping("/addTask")
    ProjectDTO addTaskToProject(@RequestParam("projectId") String projectId, @RequestParam("taskId") String taskId);

    @PostMapping("/addBug")
    ProjectDTO addBugToProject(@RequestParam("projectId") String projectId, @RequestParam("bugId") String bugId);

    @PostMapping("/addEpic")
    ProjectDTO addEpicToProject(@RequestParam("projectId") String projectId, @RequestParam("epicId") String epicId);
}
