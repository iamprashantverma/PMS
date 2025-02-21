package com.pms.TaskService.dto;


import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskInputDTO extends IssueInputDTO{
    private boolean isBlocking;
    private List<String> memberId;

}
