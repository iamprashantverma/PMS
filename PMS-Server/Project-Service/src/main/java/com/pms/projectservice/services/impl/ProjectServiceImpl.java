package com.pms.projectservice.services.impl;

import com.pms.projectservice.auth.UserContextHolder;
import com.pms.projectservice.clients.UserFeignClient;
import com.pms.projectservice.dto.ProjectDTO;
import com.pms.projectservice.dto.UserDTO;
import com.pms.projectservice.entities.Project;
import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
import com.pms.projectservice.event.EventType;
import com.pms.projectservice.event.ProjectEvent;
import com.pms.projectservice.exceptions.ResourceNotFound;
import com.pms.projectservice.producer.ProjectEventProducer;
import com.pms.projectservice.repositories.ProjectRepository;
import com.pms.projectservice.services.CloudinaryService;
import com.pms.projectservice.services.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pms.projectservice.event.EventType.PROJECT_DEADLINE_EXTENDED;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;
    private final ProjectEventProducer producer;
    private  final UserFeignClient userFeignClient;
    private final CloudinaryService cloudinaryService;
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

    /* create the project Event of type MEMBER_ASSIGNED */
    private ProjectEvent getMemberAssignedEvent(Project p,Set<String> members) {
        return ProjectEvent.builder()
                /* Project Id */
                .projectId(p.getProjectId())
                /* event type for routing */
                .eventType(EventType.MEMBER_ASSIGNED)
                /* project description */
                .description(p.getDescription())
                /* all the members who assigned into this project */
                .members(members)
                /*  admin / Tl who assigned them */
                .triggeredBy(getCurrentUserId())
                /* project title / name */
                .name(p.getTitle())
                .build();
    }

    /* create the project Event of Type PRIORITY_CHANGED */
    private ProjectEvent getPriorityChangedEvent(Project project, Priority old) {
        return  ProjectEvent.builder()
                .name(project.getTitle())
                .projectId(project.getProjectId())
                .oldPriority(old)
                .newPriority(project.getPriority())
                .updatedDate(LocalDate.now())
                /* who's update the priority of the project */
                .triggeredBy(getCurrentUserId())
                .eventType(EventType.PRIORITY_UPDATED)
                .build();
    }

    /* create the project Event of Type MEMBER_REMOVED */
    private ProjectEvent getMemberRemovedEvent(Project project, String memberId) {
        return ProjectEvent.builder()
                .name(project.getTitle())
                /* when your removed from this project */
                .createdDate(LocalDate.now())
                .members(Set.of(memberId))
                .triggeredBy(getCurrentUserId())
                .eventType(EventType.MEMBER_REMOVED)
                .build();
    }

    /* create the project event of type STATUS_UPDATED */
    private ProjectEvent getStatusChangedEvent(Project project , Status oldStatus) {
        return  ProjectEvent.builder()
                .name(project.getTitle())
                .oldStatus(oldStatus)
                .newStatus(project.getStatus())
                .eventType(EventType.STATUS_UPDATED)
                .updatedDate(LocalDate.now())
                .triggeredBy(getCurrentUserId())
                .projectId(project.getProjectId())
                .description(project.getDescription())
                .build();
    }

    /* create the project event of type  PROJECT_DEADLINE_EXTENDED */
    private ProjectEvent getDeadlineExtendedEvent(Project project , LocalDate prevDeadLine) {
        return ProjectEvent.builder()
                .name(project.getTitle())
                .projectId(project.getProjectId())
                .eventType( PROJECT_DEADLINE_EXTENDED )
                .triggeredBy(getCurrentUserId())
                .newDeadLine(project.getDeadline())
                .oldDeadLine(prevDeadLine)
                .description(project.getDescription())
                .timestamp(LocalDate.now())
                .build();


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
        log.info("project DTO:{}",newProject.getDeadline());
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


    @Override
    @Transactional
    public ProjectDTO deleteProject(String projectId) {
            Project project = getProjectEntityById(projectId) ;
            /* change the status to completed */
            project.setStatus(Status.COMPLETED);
            project.setEndDate(LocalDate.now());

            /* persist the new changes into the db */
            Project savedProject = projectRepository.save(project) ;

            return convertToProjectDTO(savedProject);
    }

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
                    project.setStartDate((LocalDate) value);
                    break;
                case "deadline":
                    project.setDeadline((LocalDate) value);
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

    @Override
    @Transactional
    public ProjectDTO extendProjectDeadline( String projectId,LocalDate localDate) {

        /* fetch the project by their id */
        Project project = getProjectEntityById(projectId);
        /* extract the prev Deadline */
        LocalDate prevDeadLine = project.getDeadline();
        /* setting up new extended date */
        project.setDeadline(localDate);
        /* saved the modified project into the db */
        Project savedProject = projectRepository.save(project);

        /* create the project event and produce  */
        ProjectEvent projectEvent = getDeadlineExtendedEvent(savedProject,prevDeadLine);
        producer.sendProjectEvent(projectEvent);

        return  convertToProjectDTO(savedProject);
    }

    /* update the status of the project */
    @Override
    @Transactional
    public ProjectDTO setProjectStatus(String  projectId, Status status) {


        Project project = getProjectEntityById(projectId);
        /* get the old status to notify the consumer */
        Status oldStatus = project.getStatus();
                /* modify the current project  status */
        project.setStatus(status);
        /* saved into modified project status into the DataBase */
        Project modifiedProject = projectRepository.save(project);

        log.info("Project Status updated successfully:{}",modifiedProject);
        /*
        * create the new event of STATUS_UPDATED type */
        ProjectEvent projectEvent = getStatusChangedEvent(modifiedProject,oldStatus);
        producer.sendProjectEvent(projectEvent);

        /* convert into the projectDto and return it*/
        return convertToProjectDTO(modifiedProject);
    }

    /* update the priority of the project */
    @Override
    @Transactional
    public ProjectDTO  updateProjectDetails(ProjectDTO projectDTO, MultipartFile file) {

        /* get the project */
        Project project = getProjectEntityById(projectDTO.getProjectId());
        /* get the old Priority */
        Priority oldPriority = project.getPriority();
       /* update the project details */
        project.setPriority(projectDTO.getPriority());
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setClientId(projectDTO.getClientId());
        /* upload the file to the cloudinary and set the url as image of this project*/
        String url = project.getImage();
        if(file != null)
            url = cloudinaryService.uploadImage(file);
        project.setImage(url);
        /* saved into the DB*/
        Project modifedProject = projectRepository.save(project);

        log.info("project priority changed:{}",modifedProject);
        /* create the projectEvent */
        ProjectEvent projectEvent = getPriorityChangedEvent(modifedProject,oldPriority);

//        producer.sendProjectEvent(projectEvent);

        /* convert into the DTO and return it*/
        return  convertToProjectDTO(modifedProject);

    }

    /* assign the members to project */
    @Override
    @Transactional
    public ProjectDTO addMembersToProject( String projectId, Set<String> member) {

        /* get the project */
        Project project = getProjectEntityById(projectId) ;
        /* here we'll write the logic to ensure that only valid member assigned to the project*/

        /* add assign the member to the project */
        for (String memberId:member) {
            try{
                UserDTO userDTO = userFeignClient.getUserById(memberId);
            } catch (Exception ex) {
                throw new ResourceNotFoundException("User not Found UserId:"+member);
            }
            project.getMemberIds().add(memberId);
        }

        /* saved the updated project into the DB */
        Project updatedProject = projectRepository.save(project);

        /* create the project event and  produce to KAFKA */
        ProjectEvent projectEvent = getMemberAssignedEvent(updatedProject,member);

//        producer.sendProjectEvent(projectEvent);

        return convertToProjectDTO(updatedProject);
    }

    /* remove a particular member from the project*/
    @Override
    @Transactional
    public ProjectDTO removeUserFromProject(String projectId, String memberId) {

        Project project = getProjectEntityById(projectId);

        /*Check if the member exists in the project*/
        if (!project.getMemberIds().contains(memberId)) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " is not part of project " + projectId);
        }

        /* Remove the member from the project*/
        project.getMemberIds().remove(memberId);
        Project modifiedProject = projectRepository.save(project);

        /*Create memberRemovedEvent and produce it*/
        ProjectEvent projectEvent = getMemberRemovedEvent(modifiedProject, memberId);
//        producer.sendProjectEvent(projectEvent);

        log.info("Member revoked successfully: {}", memberId);
        return convertToProjectDTO(modifiedProject);
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

    @Override
    public ProjectDTO addEpicInToTheProject(String projectId, String epicId) {
        Project project = getProjectEntityById(projectId);
        project.getEpicIds().add(epicId);

        Project modifiedProject = projectRepository.save(project);

        return convertToProjectDTO(modifiedProject);
    }

    @Override
    public ProjectDTO addTaskInToTheProject(String projectId, String taskId) {
       Project project = getProjectEntityById(projectId);
       project.getTaskIds().add(taskId);

       Project modifiedProject = projectRepository.save(project);

        return convertToProjectDTO(modifiedProject);
    }

    @Override
    public ProjectDTO addBugInToTheProject(String projectId, String bugId) {
        Project project = getProjectEntityById(projectId);
        project.getBugIds().add(bugId);

        Project modifiedProject = projectRepository.save(project);

        return convertToProjectDTO(modifiedProject);
    }

    @Override
    public List<ProjectDTO> findAllProject(String creatorId, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Project> projectPage = projectRepository.findByCreatorOrMemberAndStatusNotCompleted(creatorId, pageable);
        log.info("size,{}", projectPage.getContent().size());
        return projectPage.getContent().stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .toList();
    }


}
