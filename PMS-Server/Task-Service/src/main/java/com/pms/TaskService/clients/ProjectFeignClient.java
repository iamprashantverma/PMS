package com.pms.TaskService.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communicating with the Project Service.
 */
@FeignClient(name = "project-service", path = "/projects")
public interface ProjectFeignClient {

    /**
     * Retrieves a project by its ID.
     *
     * @param projectId the ID of the project to retrieve
     */
    @GetMapping
    void getProject(@RequestParam("projectId") String projectId);

    /**
     * Adds a task to the specified project.
     *
     * @param projectId the ID of the project
     * @param taskId the ID of the task to add
     */
    @PostMapping("/addTask")
    void addTaskToProject(@RequestParam("projectId") String projectId,
                          @RequestParam("taskId") String taskId);

    /**
     * Adds a bug to the specified project.
     *
     * @param projectId the ID of the project
     * @param bugId the ID of the bug to add
     */
    @PostMapping("/addBug")
    void addBugToProject(@RequestParam("projectId") String projectId,
                         @RequestParam("bugId") String bugId);

    /**
     * Adds an epic to the specified project.
     *
     * @param projectId the ID of the project
     * @param epicId the ID of the epic to add
     */
    @PostMapping("/addEpic")
    void addEpicToProject(@RequestParam("projectId") String projectId,
                          @RequestParam("epicId") String epicId);
}
