package com.pms.projectservice.services;

import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.entity.enums.Priority;
import com.pms.projectservice.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectService {

    public ProjectDTO createProject(ProjectDTO projectDTO);

    public ProjectDTO getProjectById(String projectId);

    public ProjectDTO deleteProject(String projectId);

    public ProjectDTO updateProject(ProjectDTO projectDTO);

    public List<ProjectDTO> getAllProjectByUserId(String userId);

    public String extendProjectDeadline(LocalDateTime localDateTime);

    public String setProjectStatus(Status status);


    public String setProjectPriority(Priority priority);

    public String addMemberOnProject(String member);
    public String removeUserFromProject(String projectId, String userId);


    public List<ProjectDTO> getProjectsByStatus(Status status);

    public List<ProjectDTO> searchProjectsByTitle(String title);
}
