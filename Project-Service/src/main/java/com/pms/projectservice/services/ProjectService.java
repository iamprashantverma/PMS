package com.pms.projectservice.services;

import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing projects.
 * Provides methods for creating, updating, deleting, and retrieving projects,
 * as well as managing project deadlines, status, priority, and members.
 */
public interface ProjectService {

    /**
     * Creates a new project.
     *
     * @param projectDTO the project details to create
     * @return the created project as a ProjectDTO
     */
    ProjectDTO createProject(ProjectDTO projectDTO);

    /**
     * Retrieves a project by its ID.
     *
     * @param projectId the ID of the project
     * @return the project as a ProjectDTO
     */
    ProjectDTO getProjectById(String projectId);

    /**
     * Deletes a project by its ID.
     *
     * @param projectId the ID of the project to delete
     * @return the deleted project as a ProjectDTO
     */
    ProjectDTO deleteProject(String projectId);

    /**
     * Updates the details of an existing project.
     *
     * @param projectId the ID of the project to update
     * @param info a map of fields to update with their new values
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO updateProject(String projectId, Map<String, Object> info);

    /**
     * Extends the deadline of an existing project.
     *
     * @param projectId the ID of the project to extend the deadline
     * @param localDate the new deadline date
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO extendProjectDeadline(String projectId, LocalDate localDate);

    /**
     * Sets the status of a project.
     *
     * @param projectId the ID of the project
     * @param status the new status to set
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO setProjectStatus(String projectId, Status status);

    /**
     * Sets the priority of a project.
     *
     * @param projectId the ID of the project
     * @param priority the new priority to set
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO setProjectPriority(String projectId, Priority priority);

    /**
     * Adds members to an existing project.
     *
     * @param projectId the ID of the project
     * @param members the list of member IDs to add to the project
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO addMembersToProject(String projectId, List<String> members);

    /**
     * Removes a user from a project.
     *
     * @param projectId the ID of the project
     * @param userId the ID of the user to remove
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO removeUserFromProject(String projectId, String userId);

    /**
     * Retrieves all projects with a specific status.
     *
     * @param status the status of the projects to retrieve
     * @return a list of projects with the specified status
     */
    List<ProjectDTO> getProjectsByStatus(Status status);

    /**
     * Searches for projects by title.
     *
     * @param title the title to search for
     * @return a list of projects that match the title
     */
    List<ProjectDTO> searchProjectsByTitle(String title);
}
