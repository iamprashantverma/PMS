package com.pms.projectservice.controller;

import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.entities.Project;
import com.pms.projectservice.services.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final ProjectService projectService;

    @GetMapping
    public ProjectDTO getProjectDetails(@RequestParam("projectId")String projectId) {
        return  projectService.getProjectById(projectId);
    }

    @PostMapping("/addEpic")
    private ProjectDTO addEpicInToProject(@RequestParam("projectId")String projectId,@RequestParam("epicId")String epicId) {
        return projectService.addEpicInToTheProject(projectId,epicId);
    }

    @PostMapping("/addTask")
    private ProjectDTO addTaskInToTheProject(@RequestParam("projectId")String projectId,@RequestParam("taskId")String taskId) {
        return projectService.addTaskInToTheProject(projectId,taskId);
    }

    @PostMapping("/addBug")
    private ProjectDTO addBugInToTheProject(@RequestParam("projectId")String projectId,@RequestParam("bugId")String bugId) {
        return projectService.addBugInToTheProject(projectId,bugId);
    }


}
