package com.pms.projectservice.services.impl;

import com.pms.projectservice.auth.UserContextHolder;
import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.entities.Project;
import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
import com.pms.projectservice.exceptions.ResourceNotFound;
import com.pms.projectservice.repositories.ProjectRepository;
import com.pms.projectservice.services.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;

    /* converting projectDTO projectEntity */
    private Project convertToProjectEntity(ProjectDTO projectDTO) {
        return modelMapper.map(projectDTO, Project.class);
    }

    /* converting Entity to the DTO */
    private ProjectDTO convertToProjectDTO(Project project) {
        return modelMapper.map(project, ProjectDTO.class);
    }

    /* get the id of the current user */
    public String getCurrentUserId() {
        /* extracting the current UserId */
        return UserContextHolder.getCurrentUserId();
    }

    /* get the project entity by projectID */
    private Project getProjectEntityById(String projectId) {
        /* get the Project by  projectId */
        return projectRepository.findById(projectId).orElseThrow(()->
                new ResourceNotFound("Invalid Project Id:"+projectId));
    }

    /* create the new Project*/
    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {

        /* convert the projectDTO into the project Entity */
        Project newProject = convertToProjectEntity(projectDTO) ;
        /* add the necessary information about the project e.g. Status */
        newProject.setStatus(Status.INITIATED);
        newProject.setProjectCreator(getCurrentUserId());

        /* persist the current project in to the DB*/
        Project savedProject = projectRepository.save(newProject);

        log.info("project created:{}",savedProject);

        /* return the saved project */
        return convertToProjectDTO(savedProject);

    }

    /* fetching the project by their ID */
    @Override
    public ProjectDTO getProjectById(String projectId) {
        /* fetching the project from the DB */
        Project project = getProjectEntityById(projectId);
        /* converting the project into the projectDto and return it*/
        return convertToProjectDTO(project);
    }

    /* marks the project as complete and then delete */
    @Override
    @Transactional
    public ProjectDTO deleteProject(String projectId) {
            Project project = getProjectEntityById(projectId) ;
            /* change the status to completed */
            project.setStatus(Status.COMPLETED);
            project.setEndDate(LocalDateTime.now());

            /* persist the new changes into the db */
            Project savedProject = projectRepository.save(project) ;

            return convertToProjectDTO(savedProject);
    }

    /* update the partial project details */
    @Override
    @Transactional
    public ProjectDTO updateProject(String projectId, Map<String, Object> info) {
        // Retrieve the existing project entity by projectId
        Project project = getProjectEntityById(projectId);

        /* Loop through the map and update the project fields dynamically */
        info.forEach((key, value) -> {
            switch (key) {
                case "title":
                    project.setTitle((String) value);
                    break;
                case "description":
                    project.setDescription((String) value);
                    break;
                case "startDate":
                    project.setStartDate((LocalDateTime) value);
                    break;
                case "deadline":
                    project.setDeadline((LocalDateTime) value);
                    break;
                case "extendedDate":
                    project.setExtendedDate((LocalDateTime) value);
                    break;
                case "status":
                    project.setStatus((Status) value);
                    break;
                case "priority":
                    project.setPriority((Priority) value);
                    break;
                case "projectCreator":
                    project.setProjectCreator((String) value);
                    break;
                case "clientId":
                    project.setClientId((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        // Save the updated project back to the database
        Project updatedProject = projectRepository.save(project);

        // Convert the updated Project entity to ProjectDTO and return it
        return convertToProjectDTO(updatedProject);
    }



    /* extend the project deadlines */
    @Override
    @Transactional
    public ProjectDTO extendProjectDeadline( String projectId,LocalDateTime localDateTime) {
        Project project = getProjectEntityById(projectId);
        project.setExtendedDate(localDateTime);

        Project savedProject = projectRepository.save(project);

        return  convertToProjectDTO(project);
    }

    /* update the status of the project */
    @Override
    @Transactional
    public ProjectDTO setProjectStatus(String  projectId, Status status) {
        Project project = getProjectEntityById(projectId);
        /* modify the current project  status */
        project.setStatus(status);

        /* saved into modified project status into the DataBase */
        Project modifiedProject = projectRepository.save(project);

        log.info("Project Status updated successfully:{}",modifiedProject);

        /* convert into the projectDto and return it*/
        return convertToProjectDTO(modifiedProject);
    }

    /* update the priority of the project */
    @Override
    @Transactional
    public ProjectDTO setProjectPriority(String projectId,Priority priority) {

        /* get the project */
        Project project = getProjectEntityById(projectId);

        /* update the project priority */
        project.setPriority(priority);

        /* saved into the DB*/
        Project modifedProject = projectRepository.save(project);
        log.info("project priority changed:{}",modifedProject);
        /* convert into the DTO and return it*/
        return  convertToProjectDTO(modifedProject);

    }

    /* assign the member to project */
    @Override
    @Transactional
    public ProjectDTO addMembersToProject( String projectId, List<String> member) {
        /* get the project */
        Project project = getProjectEntityById(projectId) ;
        /* here we'll write the logic to ensure that only valid member assigned to the project*/

        /* add assign the member to the project */
        for (String memberId:member) {
            project.getMembersId().add(memberId);
        }

        /* saved the updated project into the DB */
        Project updatedProject = projectRepository.save(project);

        return convertToProjectDTO(updatedProject);
    }

    /* remove the particular member from the project*/
    @Override
    @Transactional
    public ProjectDTO removeUserFromProject(String projectId, String memberId) {
        /* get the project */
        Project project = getProjectEntityById(projectId) ;

        /* removing the member from the project  */
        project.getMembersId().remove(memberId);
        Project modifedProject = projectRepository.save(project);
        log.info("member revoked successfully :{}",memberId);
        return convertToProjectDTO(modifedProject);
    }

    /* get the all projects by their status */
    @Override
    public List<ProjectDTO> getProjectsByStatus(Status status) {
        List<Project> projects = projectRepository.findAllByStatus(status);

        /* convert the projects into the project DTO */
        return projects.stream()
                .map(this::convertToProjectDTO)
                .toList();
    }

    /* find all  the projects by title */
    @Override
    public List<ProjectDTO> searchProjectsByTitle(String title) {
        List<Project> projects = projectRepository.findAllByTitle(title);
        /* convert the projects into the project DTO */
        return projects.stream()
                .map(this::convertToProjectDTO)
                .toList();
    }
}
