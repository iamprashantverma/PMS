package com.pms.TaskService.clients;

import com.pms.TaskService.dto.ProjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "project-service", path = "/projects")
public interface ProjectFeignClient {

    @GetMapping
    ProjectDTO getProject(@RequestParam("projectId") String projectId);

    @PostMapping("/addTask")
    void addTaskToProject(@RequestParam("projectId") String projectId, @RequestParam("taskId") String taskId);

    @PostMapping("/addBug")
    void addBugToProject(@RequestParam("projectId") String projectId, @RequestParam("bugId") String bugId);

    @PostMapping("/addEpic")
    void addEpicToProject(@RequestParam("projectId") String projectId, @RequestParam("epicId") String epicId);
}
