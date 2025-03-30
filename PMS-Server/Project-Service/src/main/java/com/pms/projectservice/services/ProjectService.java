package com.pms.projectservice.services;

import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.dto.ResponseDTO;
import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @param projectDTO the ID of the project
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO  updateProjectDetails(ProjectDTO projectDTO, MultipartFile file);

    /**
     * Adds members to an existing project.
     *
     * @param projectId the ID of the project
     * @param members the list of member IDs to add to the project
     * @return the updated project as a ProjectDTO
     */
    ProjectDTO addMembersToProject(String projectId, Set<String> members);

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

    /**
     * Adds an Epic to the specified Project.
     *
     * @param projectId The ID of the project to which the epic should be added.
     * @param epicId    The ID of the epic to be added.
     * @return Updated ProjectDTO with the added epic details.
     */
    ProjectDTO addEpicInToTheProject(String projectId, String epicId);

    /**
     * Adds a Task to the specified Project.
     *
     * @param projectId The ID of the project to which the task should be added.
     * @param taskId    The ID of the task to be added.
     * @return Updated ProjectDTO with the added task details.
     */
    ProjectDTO addTaskInToTheProject(String projectId, String taskId);

    /**
     * Adds a Bug to the specified Project.
     *
     * @param projectId The ID of the project to which the bug should be added.
     * @param bugId     The ID of the bug to be added.
     * @return Updated ProjectDTO with the added bug details.
     */
    ProjectDTO addBugInToTheProject(String projectId, String bugId);

    /**
     * @param  creatorId the to find all their project
     * @return List<ProjectDTO> all their project created by them or int which assigned by
     * */
    List<ProjectDTO> findAllProject(String creatorId,int page) ;


    ResponseDTO updateNotification(String projectId, Boolean flag);
}
