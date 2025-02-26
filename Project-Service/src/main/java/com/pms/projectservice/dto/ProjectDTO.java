package com.pms.projectservice.dto;

import com.pms.projectservice.entities.enums.Priority;
import com.pms.projectservice.entities.enums.Status;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private String projectId;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate deadline;

    private LocalDate extendedDate;

//  @NotNull(message = "Project status is required")
    private Status status;

    @NotNull(message = "Project priority is required")
    private Priority priority;

    @NotBlank(message = "Owner ID is required")
    private String ownerId;

    @NotBlank(message = "Client ID is required")
    private String clientId;

    private LocalDate createdAt;

    private List<String> memberId;
    private List<String> taskId;
    private List<String> milestoneId;
    private List<String> chatRoomId;
    private List<String> documentId;
}
