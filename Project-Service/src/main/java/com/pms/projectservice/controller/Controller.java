package com.pms.projectservice.controller;

import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.services.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final ProjectService projectService;

    @GetMapping
    public ProjectDTO getProjectDetails(@RequestParam(name = "projectId")String projectId) {
        return  projectService.getProjectById(projectId);
    }


}
