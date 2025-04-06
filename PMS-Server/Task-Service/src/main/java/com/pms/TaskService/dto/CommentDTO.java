package com.pms.TaskService.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data

public class CommentDTO {
    private Long commentId ;
    private String taskId;
    private String userId;
    private String message;
    private LocalDate createdAt;

}
