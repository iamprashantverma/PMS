package com.pms.projectservice.resolver;

import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.dto.ResponseDTO;
import com.pms.projectservice.entities.enums.Status;
import com.pms.projectservice.services.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ProjectResolver {

    private final ProjectService projectService;

    @QueryMapping
    public ProjectDTO getProject(@Argument("projectId") String projectId) {
        log.info("Get project request received for ID: {}", projectId);
        return projectService.getProjectById(projectId);
    }

    @QueryMapping
    public List<ProjectDTO> getProjectsByStatus(@Argument("status") Status status) {
        log.info("Fetching projects with status: {}", status);
        return projectService.getProjectsByStatus(status);
    }

    @QueryMapping
    public List<ProjectDTO> searchProjectsByTitle(@Argument("title") String title) {
        log.info("Searching projects by title: {}", title);
        return projectService.searchProjectsByTitle(title);
    }

    @MutationMapping
    public ProjectDTO createProject(@Argument("project") ProjectDTO project) {
        log.info("Create project request received: {}", project);
        return projectService.createProject(project);
    }

    @MutationMapping
    public ProjectDTO setProjectStatus(@Argument("projectId") String projectId, @Argument("status") Status status) {
        log.info("Updating project status for ID: {} to {}", projectId, status);
        return projectService.setProjectStatus(projectId, status);
    }

    @MutationMapping
    public ProjectDTO  updateProjectDetails(@Argument("project") ProjectDTO project, @Argument("image")MultipartFile file) {
        log.info("Updating project  ID: {},{}", project,file);
        return projectService.updateProjectDetails(project,file);
    }

    @MutationMapping
    public ProjectDTO extendProjectDeadline(@Argument("projectId") String projectId, @Argument("date") LocalDate date) {
        log.info("Extending project deadline for ID: {} to {}", projectId, date);
        return projectService.extendProjectDeadline(projectId, date);
    }

    @MutationMapping
    public ProjectDTO addMembersToProject(@Argument("projectId") String projectId, @Argument("members") Set<String> members) {
        log.info("Adding members {} to project ID: {}", members, projectId);
        return projectService.addMembersToProject(projectId, members);
    }

    @MutationMapping
    public ProjectDTO removeUserFromProject(@Argument("projectId") String projectId, @Argument("memberId") String memberId) {
        log.info("Removing member ID: {} from project ID: {}", memberId, projectId);
        return projectService.removeUserFromProject(projectId, memberId);
    }

    @MutationMapping
    public ProjectDTO deleteProject(@Argument("projectId") String projectId) {
        return projectService.deleteProject(projectId);
    }
    @QueryMapping
    public List<ProjectDTO> findAllProject( @Argument("pageNo") int page){
        log.info("Finding All Project for the User ID,{}",page);
        return projectService.findAllProject(page);
    }

    @MutationMapping
    public ResponseDTO updateNotification(@Argument("flag") boolean flag,@Argument("projectId") String projectId) {
            log.info(" Update Project notification settings:{}",flag);
            return projectService.updateNotification(projectId,flag);
    }
}
