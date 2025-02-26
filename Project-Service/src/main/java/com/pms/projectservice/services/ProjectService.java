package com.pms.projectservice.services;

import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProjectService {

    ProjectDTO createProject(ProjectDTO projectDTO);

    ProjectDTO getProjectById(String projectId);

    ProjectDTO deleteProject(String projectId);

    ProjectDTO updateProject(String projectId, Map<String, Object> info);

    ProjectDTO extendProjectDeadline(String projectId, LocalDate localDate);

    ProjectDTO setProjectStatus(String projectId, Status status);

    ProjectDTO setProjectPriority(String projectId, Priority priority);

    ProjectDTO addMembersToProject(String projectId, List<String> members);

    ProjectDTO removeUserFromProject(String projectId, String userId);

    List<ProjectDTO> getProjectsByStatus(Status status);

    List<ProjectDTO> searchProjectsByTitle(String title);
}
